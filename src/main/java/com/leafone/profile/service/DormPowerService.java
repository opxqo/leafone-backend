package com.leafone.profile.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafone.common.exception.BizException;
import com.leafone.profile.service.dto.*;
import com.leafone.user.mapper.StudentProfileMapper;
import com.leafone.user.model.StudentProfile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class DormPowerService {

    private static final String BASE_URL = "http://sd.wic.edu.cn/mobile";
    private static final String DEFAULT_PASSWORD = "cy@123";
    private static final long CACHE_TTL_MINUTES = 30;
    private static final long SESSION_TTL_MS = CACHE_TTL_MINUTES * 60 * 1000;
    private static final int MAX_RETRY = 1;

    // Redis 不可用时的本地 session 缓存
    private final ConcurrentHashMap<String, SessionEntry> localSessionCache = new ConcurrentHashMap<>();

    private record SessionEntry(String cookie, long expireAt) {}

    private final RestTemplate restTemplate;
    private final StudentProfileMapper studentProfileMapper;
    private final ObjectMapper objectMapper;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    // ==================== 电费公开设置 ====================

    public void toggleDormPublic(Long userId, boolean isPublic) {
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
        if (profile == null) {
            throw new BizException(40000, "请先完成学生认证");
        }
        profile.setDormPublic(isPublic ? 1 : 0);
        studentProfileMapper.updateById(profile);
    }

    private void checkDormPublic(Long userId) {
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
        if (profile == null || profile.getRoomName() == null || profile.getRoomName().isBlank()) {
            throw new BizException(40400, "该用户未绑定寝室");
        }
        if (profile.getDormPublic() == null || profile.getDormPublic() != 1) {
            throw new BizException(40300, "该用户未公开电费信息");
        }
    }

    // ==================== 绑定房间号 ====================

    public void bindRoomName(Long userId, String roomName, String password) {
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
        if (profile == null) {
            throw new BizException(40000, "请先完成学生认证");
        }
        profile.setRoomName(roomName);
        if (password != null && !password.isBlank()) {
            profile.setDormPassword(encodePassword(password));
        } else {
            profile.setDormPassword(null);
        }
        studentProfileMapper.updateById(profile);
        clearCache(roomName);
    }

    // ==================== 查询余额 ====================

    public DormPowerBalanceResponse getBalance(Long userId) {
        RoomInfo room = getRoomInfo(userId);
        String cacheKey = "dorm_power:balance:" + room.roomName;

        String cached = getFromCache(cacheKey);
        if (cached != null) {
            return parseBalanceResponse(cached, room.roomName);
        }

        String html = executeWithRetry(room, sessionCookie -> {
            IdDtd params = getIdAndDtd(sessionCookie);
            return httpGet(BASE_URL + "/mobile!mzfbfind.action?id=" + params.id() + "&dtd=" + params.dtd(), sessionCookie);
        });

        BigDecimal basicBalance = extractValue(html, "基本账户");
        BigDecimal subsidyBalance = extractValue(html, "补助账户");

        DormPowerBalanceResponse resp = new DormPowerBalanceResponse();
        resp.setRoomName(room.roomName);
        resp.setBasicBalance(basicBalance);
        resp.setSubsidyBalance(subsidyBalance);

        putToCache(cacheKey, basicBalance + "," + subsidyBalance);
        return resp;
    }

    // ==================== 查询月用电量 ====================

    public DormPowerMonthlyResponse getMonthly(Long userId) {
        RoomInfo room = getRoomInfo(userId);
        String cacheKey = "dorm_power:monthly:" + room.roomName;

        String cached = getFromCache(cacheKey);
        if (cached != null) {
            return parseMonthlyResponse(cached, room.roomName);
        }

        String html = executeWithRetry(room, sessionCookie ->
                httpGet(BASE_URL + "/mobile!rptmonth.action", sessionCookie));

        List<String> months = extractCategories(html);
        List<BigDecimal> usage = extractChartData(html);

        DormPowerMonthlyResponse resp = new DormPowerMonthlyResponse();
        resp.setRoomName(room.roomName);
        resp.setMonths(months);
        resp.setUsage(usage);

        putToCache(cacheKey, serializeLabelsAndUsage(months, usage));
        return resp;
    }

    // ==================== 查询日用电量 ====================

    public DormPowerDailyResponse getDaily(Long userId) {
        RoomInfo room = getRoomInfo(userId);
        String cacheKey = "dorm_power:daily:" + room.roomName;

        String cached = getFromCache(cacheKey);
        if (cached != null) {
            return parseDailyResponse(cached, room.roomName);
        }

        String html = executeWithRetry(room, sessionCookie ->
                httpGet(BASE_URL + "/mobile!rptdata.action", sessionCookie));

        List<String> dates = extractCategories(html);
        List<BigDecimal> usage = extractChartData(html);

        DormPowerDailyResponse resp = new DormPowerDailyResponse();
        resp.setRoomName(room.roomName);
        resp.setDates(dates);
        resp.setUsage(usage);

        putToCache(cacheKey, serializeLabelsAndUsage(dates, usage));
        return resp;
    }

    // ==================== 查询72小时用电量 ====================

    public DormPowerHourlyResponse getHourly(Long userId) {
        RoomInfo room = getRoomInfo(userId);
        String cacheKey = "dorm_power:hourly:" + room.roomName;

        String cached = getFromCache(cacheKey);
        if (cached != null) {
            return parseHourlyResponse(cached, room.roomName);
        }

        String html = executeWithRetry(room, sessionCookie ->
                httpGet(BASE_URL + "/mobile!rpt72.action", sessionCookie));

        List<BigDecimal> usage = extractChartData(html);

        DormPowerHourlyResponse resp = new DormPowerHourlyResponse();
        resp.setRoomName(room.roomName);
        resp.setUsage(usage);

        putToCache(cacheKey, serializeHourly(usage));
        return resp;
    }

    // ==================== 查看他人电费（需公开） ====================

    public DormPowerBalanceResponse getPublicBalance(Long userId) {
        checkDormPublic(userId);
        return getBalance(userId);
    }

    public DormPowerMonthlyResponse getPublicMonthly(Long userId) {
        checkDormPublic(userId);
        return getMonthly(userId);
    }

    public DormPowerDailyResponse getPublicDaily(Long userId) {
        checkDormPublic(userId);
        return getDaily(userId);
    }

    public DormPowerHourlyResponse getPublicHourly(Long userId) {
        checkDormPublic(userId);
        return getHourly(userId);
    }

    // ==================== 带重试的请求执行 ====================

    @FunctionalInterface
    private interface SessionRequest<T> {
        T execute(String sessionCookie);
    }

    private <T> T executeWithRetry(RoomInfo room, SessionRequest<T> request) {
        String sessionCookie = loginAndGetSession(room);
        for (int attempt = 0; attempt <= MAX_RETRY; attempt++) {
            try {
                return request.execute(sessionCookie);
            } catch (Exception e) {
                if (attempt < MAX_RETRY && isSessionExpired(e)) {
                    log.info("Session expired for room {}, re-login", room.roomName);
                    invalidateSession(room.roomName);
                    sessionCookie = loginAndGetSession(room);
                } else {
                    throw e;
                }
            }
        }
        throw new BizException(50000, "电费系统请求失败");
    }

    private boolean isSessionExpired(Exception e) {
        // BizException 50000 通常是 HTTP 请求失败或数据解析异常
        if (e instanceof BizException biz) {
            return biz.getCode() == 50000;
        }
        return true;
    }

    // ==================== 内部方法 ====================

    private record RoomInfo(String roomName, String password) {}

    private RoomInfo getRoomInfo(Long userId) {
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
        if (profile == null || profile.getRoomName() == null || profile.getRoomName().isBlank()) {
            throw new BizException(40000, "请先绑定寝室房间号");
        }
        String password = profile.getDormPassword() != null
                ? decodePassword(profile.getDormPassword())
                : DEFAULT_PASSWORD;
        return new RoomInfo(profile.getRoomName(), password);
    }

    private String loginAndGetSession(RoomInfo room) {
        String sessionCacheKey = "dorm_power:session:" + room.roomName;

        // 1. 尝试从 Redis 获取
        if (redisTemplate != null) {
            try {
                String cached = redisTemplate.opsForValue().get(sessionCacheKey);
                if (cached != null) return cached;
            } catch (Exception e) {
                log.warn("Redis session get failed: {}", e.getMessage());
            }
        }

        // 2. 尝试从本地缓存获取
        SessionEntry localEntry = localSessionCache.get(sessionCacheKey);
        if (localEntry != null && localEntry.expireAt() > System.currentTimeMillis()) {
            return localEntry.cookie();
        }

        // 3. 登录获取新 session
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("j_username", room.roomName);
        body.add("j_kxiotdata", room.password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    BASE_URL + "/mobile!main.action",
                    HttpMethod.POST,
                    request,
                    String.class);
        } catch (Exception e) {
            log.error("Login failed for room: {}", room.roomName, e);
            throw new BizException(50000, "电费系统登录失败，请检查房间号和密码是否正确");
        }

        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies == null || cookies.isEmpty()) {
            throw new BizException(50000, "电费系统登录失败：密码可能不正确");
        }

        String jsessionid = cookies.stream()
                .filter(c -> c.startsWith("JSESSIONID="))
                .map(c -> {
                    int end = c.indexOf(';');
                    return end > 0 ? c.substring(0, end) : c;
                })
                .findFirst()
                .orElseThrow(() -> new BizException(50000, "电费系统登录失败：密码可能不正确"));

        // 4. 写入 Redis + 本地缓存
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(sessionCacheKey, jsessionid, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.warn("Redis session set failed: {}", e.getMessage());
            }
        }
        localSessionCache.put(sessionCacheKey, new SessionEntry(jsessionid, System.currentTimeMillis() + SESSION_TTL_MS));

        return jsessionid;
    }

    private void invalidateSession(String roomName) {
        String key = "dorm_power:session:" + roomName;
        localSessionCache.remove(key);
        if (redisTemplate != null) {
            try {
                redisTemplate.delete(key);
            } catch (Exception e) {
                log.warn("Redis session delete failed: {}", e.getMessage());
            }
        }
    }

    private record IdDtd(String id, String dtd) {}

    private IdDtd getIdAndDtd(String sessionCookie) {
        String html = httpGet(BASE_URL + "/mobile!main.action", sessionCookie);

        Pattern pattern = Pattern.compile("mzfbfind\\.action\\?id=(\\d+)&dtd=(\\d+)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return new IdDtd(matcher.group(1), matcher.group(2));
        }

        throw new BizException(50000, "电费系统返回数据异常，密码可能不正确");
    }

    private String httpGet(String url, String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionCookie);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("HTTP GET failed: {}", url, e);
            throw new BizException(50000, "电费系统请求失败");
        }
    }

    // ==================== HTML 解析 ====================

    private BigDecimal extractValue(String html, String label) {
        Pattern pattern = Pattern.compile(label + "[：:]([0-9.]+)");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return new BigDecimal(matcher.group(1));
        }
        return BigDecimal.ZERO;
    }

    private List<String> extractCategories(String html) {
        Pattern pattern = Pattern.compile("categories:\\s*\\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            String raw = matcher.group(1);
            Pattern itemPattern = Pattern.compile("'([^']+)'");
            Matcher itemMatcher = itemPattern.matcher(raw);
            List<String> items = new ArrayList<>();
            while (itemMatcher.find()) {
                items.add(itemMatcher.group(1));
            }
            return items;
        }
        return List.of();
    }

    /**
     * 从 chart JS 中提取 series data 数组。
     * 匹配 series:[{...data:[...]...}] 或独立的 data:[...]。
     */
    private List<BigDecimal> extractChartData(String html) {
        // 优先匹配 series 块内的 data（避免误匹配其他 data）
        Pattern seriesPattern = Pattern.compile("series:\\s*\\[\\{[^}]*data:\\s*\\[([0-9.,\\s]+)\\]");
        Matcher seriesMatcher = seriesPattern.matcher(html);
        if (seriesMatcher.find()) {
            return parseNumberArray(seriesMatcher.group(1));
        }

        // fallback: 匹配独立的 data:[...]
        Pattern dataPattern = Pattern.compile("data:\\s*\\[([0-9.,\\s]+)\\]");
        Matcher dataMatcher = dataPattern.matcher(html);
        if (dataMatcher.find()) {
            return parseNumberArray(dataMatcher.group(1));
        }

        return List.of();
    }

    private List<BigDecimal> parseNumberArray(String raw) {
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(BigDecimal::new)
                .toList();
    }

    // ==================== 密码加解密 ====================

    private String encodePassword(String password) {
        return Base64.getEncoder().encodeToString(
                ("leafone:" + password).getBytes(StandardCharsets.UTF_8));
    }

    private String decodePassword(String encoded) {
        try {
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            if (decoded.startsWith("leafone:")) {
                return decoded.substring(8);
            }
            return DEFAULT_PASSWORD;
        } catch (Exception e) {
            return DEFAULT_PASSWORD;
        }
    }

    // ==================== 缓存辅助 ====================

    private String getFromCache(String key) {
        if (redisTemplate == null) return null;
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.warn("Redis get failed for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    private void putToCache(String key, String value) {
        if (redisTemplate == null) return;
        try {
            redisTemplate.opsForValue().set(key, value, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("Redis set failed for key {}: {}", key, e.getMessage());
        }
    }

    private void clearCache(String roomName) {
        localSessionCache.remove("dorm_power:session:" + roomName);
        if (redisTemplate == null) return;
        try {
            redisTemplate.delete("dorm_power:session:" + roomName);
            redisTemplate.delete("dorm_power:balance:" + roomName);
            redisTemplate.delete("dorm_power:monthly:" + roomName);
            redisTemplate.delete("dorm_power:daily:" + roomName);
            redisTemplate.delete("dorm_power:hourly:" + roomName);
        } catch (Exception e) {
            log.warn("Redis clearCache failed for room {}: {}", roomName, e.getMessage());
        }
    }

    // ==================== 序列化/反序列化 ====================

    private String serializeLabelsAndUsage(List<String> labels, List<BigDecimal> usage) {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("labels", labels);
            map.put("usage", usage.stream().map(BigDecimal::toPlainString).toList());
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    private DormPowerBalanceResponse parseBalanceResponse(String cached, String roomName) {
        String[] parts = cached.split(",");
        DormPowerBalanceResponse resp = new DormPowerBalanceResponse();
        resp.setRoomName(roomName);
        resp.setBasicBalance(new BigDecimal(parts[0]));
        resp.setSubsidyBalance(parts.length > 1 ? new BigDecimal(parts[1]) : BigDecimal.ZERO);
        return resp;
    }

    private DormPowerMonthlyResponse parseMonthlyResponse(String cached, String roomName) {
        try {
            Map<String, Object> map = objectMapper.readValue(cached, new TypeReference<>() {});
            DormPowerMonthlyResponse resp = new DormPowerMonthlyResponse();
            resp.setRoomName(roomName);
            resp.setMonths((List<String>) map.get("labels"));
            resp.setUsage(((List<String>) map.get("usage")).stream().map(BigDecimal::new).toList());
            return resp;
        } catch (JsonProcessingException e) {
            throw new BizException(50000, "缓存数据解析失败");
        }
    }

    private DormPowerDailyResponse parseDailyResponse(String cached, String roomName) {
        try {
            Map<String, Object> map = objectMapper.readValue(cached, new TypeReference<>() {});
            DormPowerDailyResponse resp = new DormPowerDailyResponse();
            resp.setRoomName(roomName);
            resp.setDates((List<String>) map.get("labels"));
            resp.setUsage(((List<String>) map.get("usage")).stream().map(BigDecimal::new).toList());
            return resp;
        } catch (JsonProcessingException e) {
            throw new BizException(50000, "缓存数据解析失败");
        }
    }

    private String serializeHourly(List<BigDecimal> usage) {
        try {
            return objectMapper.writeValueAsString(usage.stream().map(BigDecimal::toPlainString).toList());
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private DormPowerHourlyResponse parseHourlyResponse(String cached, String roomName) {
        try {
            List<String> usageStr = objectMapper.readValue(cached, new TypeReference<>() {});
            DormPowerHourlyResponse resp = new DormPowerHourlyResponse();
            resp.setRoomName(roomName);
            resp.setUsage(usageStr.stream().map(BigDecimal::new).toList());
            return resp;
        } catch (JsonProcessingException e) {
            throw new BizException(50000, "缓存数据解析失败");
        }
    }
}
