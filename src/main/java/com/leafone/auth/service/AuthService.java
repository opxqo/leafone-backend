package com.leafone.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.leafone.auth.mapper.UserMapper;
import com.leafone.auth.model.User;
import com.leafone.auth.service.dto.*;
import com.leafone.common.exception.BizException;
import com.leafone.common.util.JwtUtil;
import com.leafone.user.mapper.StudentProfileMapper;
import com.leafone.user.model.StudentProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    @Value("${leafone.wechat.app-id}")
    private String wechatAppId;

    @Value("${leafone.wechat.app-secret}")
    private String wechatAppSecret;

    public LoginResponse login(LoginRequest request) {
        String account = request.getStudentNo() != null ? request.getStudentNo() : request.getPhone();
        if (account == null) {
            throw new BizException(40000, "Please provide student number or phone");
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(User::getStudentNo, account).or().eq(User::getPhone, account));
        User user = userMapper.selectOne(wrapper);
        if (user == null || user.getPasswordHash() == null) {
            throw new BizException(40100, "User not found");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BizException(40100, "Incorrect password");
        }

        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        return buildLoginResponse(user);
    }

    private static final List<String> DEFAULT_AVATARS = List.of(
            "https://image.opxqo.cn/avatar/eg/001.webp",
            "https://image.opxqo.cn/avatar/eg/002.webp",
            "https://image.opxqo.cn/avatar/eg/003.webp",
            "https://image.opxqo.cn/avatar/eg/004.webp",
            "https://image.opxqo.cn/avatar/eg/005.webp",
            "https://image.opxqo.cn/avatar/eg/006.webp"
    );

    private final Random random = new Random();

    @Transactional(rollbackFor = Exception.class)
    public LoginResponse wechatLogin(WechatLoginRequest request) {
        // 用 wx.login() 的 code 调用 auth.code2Session 获取 openid
        String sessionUrl = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                wechatAppId, wechatAppSecret, request.getCode());
        String respStr;
        try {
            respStr = restTemplate.getForObject(sessionUrl, String.class);
        } catch (Exception e) {
            log.error("code2Session request failed", e);
            throw new BizException(50000, "微信登录网络异常: " + e.getMessage());
        }
        Map<String, Object> sessionResp;
        try {
            sessionResp = new com.fasterxml.jackson.databind.ObjectMapper().readValue(respStr, Map.class);
        } catch (Exception e) {
            throw new BizException(50000, "微信登录响应解析失败");
        }
        if (sessionResp == null || sessionResp.get("openid") == null) {
            String errmsg = sessionResp != null ? String.valueOf(sessionResp.get("errmsg")) : "unknown";
            throw new BizException(40100, "微信登录失败: " + errmsg);
        }

        String openid = (String) sessionResp.get("openid");
        String unionid = (String) sessionResp.get("unionid");

        // 根据 openid 查找用户
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        boolean isNew = false;
        if (user == null) {
            // 新用户：创建账号，随机头像和昵称
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickname("微信用户" + (1000 + random.nextInt(9000)));
            user.setAvatarUrl(DEFAULT_AVATARS.get(random.nextInt(DEFAULT_AVATARS.size())));
            user.setRole("USER");
            user.setStatus(1);
            userMapper.insert(user);
            isNew = true;
        }

        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        LoginResponse response = buildLoginResponse(user);
        response.setNewUser(isNew);

        // 检查学生认证状态
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, user.getId()));
        response.setStudentVerified(profile != null && profile.getVerified() == 1);

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public void verifyStudent(StudentVerificationRequest request, Long userId) {
        if (userId == null) {
            throw new BizException(40100, "请先登录");
        }

        // 检查学号是否已被其他用户认证
        LambdaQueryWrapper<StudentProfile> check = new LambdaQueryWrapper<>();
        check.eq(StudentProfile::getStudentNo, request.getStudentNo())
             .eq(StudentProfile::getVerified, 1);
        if (studentProfileMapper.selectCount(check) > 0) {
            throw new BizException(40900, "该学号已被其他用户认证");
        }

        // 更新或创建学生信息
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));

        if (profile == null) {
            profile = new StudentProfile();
            profile.setUserId(userId);
            profile.setRealName(request.getRealName());
            profile.setStudentNo(request.getStudentNo());
            profile.setCollege(request.getCollege());
            profile.setMajor(request.getMajor());
            profile.setGrade(request.getGrade());
            profile.setVerified(0); // 待审核
            studentProfileMapper.insert(profile);
        } else {
            profile.setRealName(request.getRealName());
            profile.setStudentNo(request.getStudentNo());
            profile.setCollege(request.getCollege());
            profile.setMajor(request.getMajor());
            profile.setGrade(request.getGrade());
            profile.setVerified(0); // 重新提交审核
            studentProfileMapper.updateById(profile);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        LambdaQueryWrapper<User> check = new LambdaQueryWrapper<>();
        check.eq(User::getStudentNo, request.getStudentNo());
        if (userMapper.selectCount(check) > 0) {
            throw new BizException(40900, "Student number already registered");
        }

        User user = new User();
        user.setStudentNo(request.getStudentNo());
        user.setPhone(request.getPhone());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getRealName());
        user.setAvatarUrl(DEFAULT_AVATARS.get(random.nextInt(DEFAULT_AVATARS.size())));
        user.setRole("USER");
        user.setStatus(1);
        user.setOpenid(null);
        userMapper.insert(user);

        StudentProfile profile = new StudentProfile();
        profile.setUserId(user.getId());
        profile.setRealName(request.getRealName());
        profile.setStudentNo(request.getStudentNo());
        profile.setCollege(request.getCollege());
        profile.setMajor(request.getMajor());
        profile.setGrade(request.getGrade());
        profile.setVerified(0);
        studentProfileMapper.insert(profile);
    }

    public LoginResponse refreshToken(String refreshToken) {
        // Validate refresh token
        Long userId;
        try {
            var claims = jwtUtil.parseToken(refreshToken);
            if (!"refresh".equals(claims.get("type", String.class))) {
                throw new BizException(40100, "Invalid refresh token");
            }
            userId = Long.parseLong(claims.getSubject());
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(40100, "Invalid or expired refresh token");
        }

        // Check Redis
        if (redisTemplate != null) {
            String storedUserId = redisTemplate.opsForValue().get("auth:refresh:" + refreshToken);
            if (storedUserId == null) {
                throw new BizException(40100, "Refresh token revoked or expired");
            }
            redisTemplate.delete("auth:refresh:" + refreshToken);
            redisTemplate.opsForSet().remove("auth:user_tokens:" + storedUserId, refreshToken);
        }

        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");

        return buildLoginResponse(user);
    }

    public void logout(Long userId) {
        if (redisTemplate != null) {
            try {
                String userTokensKey = "auth:user_tokens:" + userId;
                var tokens = redisTemplate.opsForSet().members(userTokensKey);
                if (tokens != null && !tokens.isEmpty()) {
                    for (String token : tokens) {
                        redisTemplate.delete("auth:refresh:" + token);
                    }
                    redisTemplate.delete(userTokensKey);
                }
            } catch (Exception e) {
                log.warn("Failed to clean refresh token on logout: {}", e.getMessage());
            }
        }
    }

    private LoginResponse buildLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setAccessToken(jwtUtil.generateAccessToken(user.getId(), user.getRole()));
        response.setRefreshToken(jwtUtil.generateRefreshToken(user.getId()));
        response.setUserId(user.getId());
        response.setRole(user.getRole());

        if (redisTemplate != null) {
            try {
                String refreshToken = response.getRefreshToken();
                String userIdStr = String.valueOf(user.getId());
                redisTemplate.opsForValue().set(
                        "auth:refresh:" + refreshToken,
                        userIdStr,
                        7, TimeUnit.DAYS);
                redisTemplate.opsForSet().add("auth:user_tokens:" + userIdStr, refreshToken);
                redisTemplate.expire("auth:user_tokens:" + userIdStr, 7, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("Failed to store refresh token in Redis: {}", e.getMessage());
            }
        }
        return response;
    }
}
