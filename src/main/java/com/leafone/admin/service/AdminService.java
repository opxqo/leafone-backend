package com.leafone.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafone.admin.service.dto.*;
import com.leafone.auth.mapper.UserMapper;
import com.leafone.auth.model.User;
import com.leafone.comment.mapper.PostCommentMapper;
import com.leafone.comment.model.PostComment;
import com.leafone.common.exception.BizException;
import com.leafone.common.response.PageResult;
import com.leafone.message.mapper.MessageMapper;
import com.leafone.message.model.Message;
import com.leafone.module.mapper.ModuleMapper;
import com.leafone.module.model.Module;
import com.leafone.post.mapper.HomeHeadlineMapper;
import com.leafone.post.mapper.PostMapper;
import com.leafone.post.mapper.TopicMapper;
import com.leafone.post.model.HomeHeadline;
import com.leafone.post.model.Post;
import com.leafone.post.model.Topic;
import com.leafone.profile.mapper.FeedbackMapper;
import com.leafone.profile.model.Feedback;
import com.leafone.user.mapper.StudentProfileMapper;
import com.leafone.user.model.StudentProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.Objects;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final FeedbackMapper feedbackMapper;
    private final ModuleMapper moduleMapper;
    private final TopicMapper topicMapper;
    private final HomeHeadlineMapper homeHeadlineMapper;
    private final PostCommentMapper postCommentMapper;
    private final MessageMapper messageMapper;
    private final StudentProfileMapper studentProfileMapper;

    // ==================== 帖子管理 ====================

    public PageResult<Post> adminPosts(Integer status, int page, int pageSize) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(Post::getStatus, status);
        wrapper.orderByDesc(Post::getCreatedAt);

        Page<Post> result = postMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    public void updatePostStatus(Long postId, Integer status) {
        if (status == null || (status != 1 && status != 2 && status != 3)) {
            throw new BizException(40000, "无效的状态值，仅支持 1=正常/2=隐藏/3=删除");
        }
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BizException(40400, "Post not found");
        post.setStatus(status);
        postMapper.updateById(post);
    }

    public void togglePostPin(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BizException(40400, "Post not found");
        post.setIsPinned(post.getIsPinned() != null && post.getIsPinned() == 1 ? 0 : 1);
        postMapper.updateById(post);
    }

    public void togglePostFeature(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BizException(40400, "Post not found");
        post.setIsFeatured(post.getIsFeatured() != null && post.getIsFeatured() == 1 ? 0 : 1);
        postMapper.updateById(post);
    }

    // ==================== 用户管理 ====================

    public PageResult<User> adminUsers(String keyword, String role, Integer status, int page, int pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(User::getNickname, keyword)
                    .or().like(User::getStudentNo, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (role != null && !role.isBlank()) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreatedAt);

        Page<User> result = userMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    public User userDetail(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");
        return user;
    }

    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");
        userMapper.deleteById(userId);
    }

    public void banUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");
        user.setStatus(2);
        userMapper.updateById(user);
    }

    public void unbanUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");
        user.setStatus(1);
        userMapper.updateById(user);
    }

    public void updateUserRole(Long userId, String role) {
        if (!"USER".equals(role) && !"ADMIN".equals(role) && !"ORGANIZER".equals(role)) {
            throw new BizException(40000, "无效的角色值，仅支持 USER/ADMIN/ORGANIZER");
        }
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");
        user.setRole(role);
        userMapper.updateById(user);
    }

    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getStudentNo() != null) user.setStudentNo(request.getStudentNo());
        if (request.getDorm() != null) user.setDorm(request.getDorm());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getGender() != null) user.setGender(request.getGender());
        userMapper.updateById(user);
    }

    public void verifyStudent(Long userId, boolean approved) {
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));
        if (profile == null) throw new BizException(40400, "该用户未提交学生认证");
        profile.setVerified(approved ? 1 : 2);
        studentProfileMapper.updateById(profile);
    }

    public PageResult<VerificationItem> adminVerifications(Integer verified, int page, int pageSize) {
        LambdaQueryWrapper<StudentProfile> wrapper = new LambdaQueryWrapper<>();
        if (verified != null) {
            wrapper.eq(StudentProfile::getVerified, verified);
        }
        wrapper.orderByDesc(StudentProfile::getCreatedAt);

        Page<StudentProfile> result = studentProfileMapper.selectPage(new Page<>(page, pageSize), wrapper);

        List<Long> userIds = result.getRecords().stream()
                .map(StudentProfile::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Map<Long, User> userMap = userIds.isEmpty()
                ? Map.of()
                : userMapper.selectBatchIds(userIds).stream()
                    .collect(java.util.stream.Collectors.toMap(User::getId, u -> u));

        List<VerificationItem> items = result.getRecords().stream()
                .map(profile -> {
                    User user = userMap.get(profile.getUserId());
                    return user != null ? VerificationItem.from(user, profile) : null;
                })
                .filter(Objects::nonNull)
                .toList();

        return PageResult.of(items, page, pageSize, result.getTotal());
    }

    // ==================== 版块管理 ====================

    public List<Module> adminModules(Integer enabled) {
        LambdaQueryWrapper<Module> wrapper = new LambdaQueryWrapper<>();
        if (enabled != null) {
            wrapper.eq(Module::getEnabled, enabled);
        }
        wrapper.orderByAsc(Module::getSortOrder).orderByDesc(Module::getCreatedAt);
        return moduleMapper.selectList(wrapper);
    }

    public Module createModule(ModuleCreateRequest request) {
        Module module = new Module();
        module.setCode(request.getCode());
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        module.setIconKey(request.getIconKey());
        module.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        module.setPostCountToday(0);
        module.setEnabled(request.getEnabled() != null ? request.getEnabled() : 1);
        moduleMapper.insert(module);
        return module;
    }

    public void updateModule(Long moduleId, ModuleCreateRequest request) {
        Module module = moduleMapper.selectById(moduleId);
        if (module == null) throw new BizException(40400, "Module not found");
        module.setCode(request.getCode());
        module.setName(request.getName());
        module.setDescription(request.getDescription());
        module.setIconKey(request.getIconKey());
        if (request.getSortOrder() != null) module.setSortOrder(request.getSortOrder());
        if (request.getEnabled() != null) module.setEnabled(request.getEnabled());
        moduleMapper.updateById(module);
    }

    public void deleteModule(Long moduleId) {
        moduleMapper.deleteById(moduleId);
    }

    // ==================== 话题管理 ====================

    public List<Topic> adminTopics(Integer enabled) {
        LambdaQueryWrapper<Topic> wrapper = new LambdaQueryWrapper<>();
        if (enabled != null) {
            wrapper.eq(Topic::getEnabled, enabled);
        }
        wrapper.orderByDesc(Topic::getHeatScore).orderByDesc(Topic::getCreatedAt);
        return topicMapper.selectList(wrapper);
    }

    public Topic createTopic(TopicCreateRequest request) {
        Topic topic = new Topic();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setHeatScore(request.getHeatScore() != null ? request.getHeatScore() : 0);
        topic.setPostCount(0);
        topic.setEnabled(request.getEnabled() != null ? request.getEnabled() : 1);
        topicMapper.insert(topic);
        return topic;
    }

    public void updateTopic(Long topicId, TopicCreateRequest request) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) throw new BizException(40400, "Topic not found");
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        if (request.getHeatScore() != null) topic.setHeatScore(request.getHeatScore());
        if (request.getEnabled() != null) topic.setEnabled(request.getEnabled());
        topicMapper.updateById(topic);
    }

    public void deleteTopic(Long topicId) {
        topicMapper.deleteById(topicId);
    }

    // ==================== 头条管理 ====================

    public PageResult<HomeHeadline> adminHeadlines(int page, int pageSize) {
        Page<HomeHeadline> result = homeHeadlineMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<HomeHeadline>().orderByDesc(HomeHeadline::getIsPinned)
                        .orderByDesc(HomeHeadline::getCreatedAt));
        return PageResult.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    public HomeHeadline createHeadline(HeadlineCreateRequest request) {
        HomeHeadline headline = new HomeHeadline();
        headline.setTitle(request.getTitle());
        headline.setSummary(request.getSummary());
        headline.setCoverUrl(request.getCoverUrl());
        headline.setTargetType(request.getTargetType());
        headline.setTargetId(request.getTargetId());
        headline.setExternalUrl(request.getExternalUrl());
        headline.setIsPinned(request.getIsPinned() != null ? request.getIsPinned() : 0);
        headline.setViewCount(0);
        headline.setPublishedAt(LocalDateTime.now());
        homeHeadlineMapper.insert(headline);
        return headline;
    }

    public void updateHeadline(Long headlineId, HeadlineCreateRequest request) {
        HomeHeadline headline = homeHeadlineMapper.selectById(headlineId);
        if (headline == null) throw new BizException(40400, "Headline not found");
        headline.setTitle(request.getTitle());
        headline.setSummary(request.getSummary());
        headline.setCoverUrl(request.getCoverUrl());
        headline.setTargetType(request.getTargetType());
        headline.setTargetId(request.getTargetId());
        headline.setExternalUrl(request.getExternalUrl());
        if (request.getIsPinned() != null) headline.setIsPinned(request.getIsPinned());
        homeHeadlineMapper.updateById(headline);
    }

    public void deleteHeadline(Long headlineId) {
        homeHeadlineMapper.deleteById(headlineId);
    }

    // ==================== 评论管理 ====================

    public PageResult<PostComment> adminComments(Long postId, Long userId, int page, int pageSize) {
        LambdaQueryWrapper<PostComment> wrapper = new LambdaQueryWrapper<>();
        if (postId != null) wrapper.eq(PostComment::getPostId, postId);
        if (userId != null) wrapper.eq(PostComment::getUserId, userId);
        wrapper.orderByDesc(PostComment::getCreatedAt);

        Page<PostComment> result = postCommentMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    public void deleteComment(Long commentId) {
        PostComment comment = postCommentMapper.selectById(commentId);
        if (comment == null) throw new BizException(40400, "Comment not found");
        comment.setStatus(3);
        postCommentMapper.updateById(comment);
        postMapper.decrementCommentCount(comment.getPostId());
    }

    public void updateCommentStatus(Long commentId, Integer status) {
        if (status == null || (status != 1 && status != 2 && status != 3)) {
            throw new BizException(40000, "无效的状态值，仅支持 1=正常/2=隐藏/3=删除");
        }
        PostComment comment = postCommentMapper.selectById(commentId);
        if (comment == null) throw new BizException(40400, "Comment not found");
        comment.setStatus(status);
        postCommentMapper.updateById(comment);
    }

    // ==================== 反馈处理 ====================

    public PageResult<Feedback> adminFeedbacks(String status, int page, int pageSize) {
        LambdaQueryWrapper<Feedback> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isBlank()) {
            wrapper.eq(Feedback::getStatus, status);
        }
        wrapper.orderByDesc(Feedback::getCreatedAt);
        Page<Feedback> result = feedbackMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(result.getRecords(), page, pageSize, result.getTotal());
    }

    public void updateFeedbackStatus(Long feedbackId, String status) {
        if (!"PENDING".equals(status) && !"RESOLVED".equals(status) && !"REJECTED".equals(status)) {
            throw new BizException(40000, "无效的状态值，仅支持 PENDING/RESOLVED/REJECTED");
        }
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) throw new BizException(40400, "Feedback not found");
        feedback.setStatus(status);
        feedbackMapper.updateById(feedback);
    }

    // ==================== 数据统计 ====================

    public AdminStatsResponse stats() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);

        AdminStatsResponse resp = new AdminStatsResponse();
        resp.setTotalUsers(userMapper.selectCount(null));
        resp.setTodayNewUsers(userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreatedAt, todayStart)));
        resp.setTotalPosts(postMapper.selectCount(null));
        resp.setTodayNewPosts(postMapper.selectCount(
                new LambdaQueryWrapper<Post>().ge(Post::getCreatedAt, todayStart)));
        resp.setTotalComments(postCommentMapper.selectCount(null));
        resp.setTodayNewComments(postCommentMapper.selectCount(
                new LambdaQueryWrapper<PostComment>().ge(PostComment::getCreatedAt, todayStart)));
        resp.setPendingPosts(postMapper.selectCount(
                new LambdaQueryWrapper<Post>().eq(Post::getStatus, 0)));
        resp.setPendingFeedbacks(feedbackMapper.selectCount(
                new LambdaQueryWrapper<Feedback>().eq(Feedback::getStatus, "PENDING")));
        resp.setBannedUsers(userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getStatus, 2)));
        return resp;
    }

    // ==================== 通知管理 ====================

    public void sendNotification(NotificationSendRequest request) {
        List<Long> targetUserIds;
        if (request.getUserIds() != null && !request.getUserIds().isEmpty()) {
            targetUserIds = request.getUserIds();
        } else {
            List<User> users = userMapper.selectList(
                    new LambdaQueryWrapper<User>().select(User::getId).eq(User::getStatus, 1));
            targetUserIds = users.stream().map(User::getId).toList();
        }

        if (targetUserIds.isEmpty()) return;

        List<Message> messages = targetUserIds.stream().map(uid -> {
            Message msg = new Message();
            msg.setId(com.baomidou.mybatisplus.core.toolkit.IdWorker.getId());
            msg.setUserId(uid);
            msg.setSenderId(null);
            msg.setType("SYSTEM");
            msg.setTitle(request.getTitle());
            msg.setContent(request.getContent());
            msg.setTargetType(request.getTargetType());
            msg.setTargetId(request.getTargetId());
            return msg;
        }).toList();

        messageMapper.insertBatch(messages);
    }

    // ==================== 缓存监控 ====================

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    public CacheStatusResponse cacheStatus() {
        CacheStatusResponse resp = new CacheStatusResponse();

        if (redisTemplate == null) {
            resp.setAvailable(false);
            return resp;
        }

        try {
            // 测试连接
            redisTemplate.getConnectionFactory().getConnection().ping();
            resp.setAvailable(true);
        } catch (Exception e) {
            log.warn("Redis connection failed: {}", e.getMessage());
            resp.setAvailable(false);
            return resp;
        }

        // 获取 Redis 服务器信息
        resp.setInfo(getRedisInfo());

        // 获取电费相关缓存
        resp.setDormPowerCaches(scanCacheEntries("dorm_power:*"));

        // 获取认证相关缓存
        resp.setAuthCaches(scanCacheEntries("auth:*"));

        return resp;
    }

    private CacheStatusResponse.RedisInfo getRedisInfo() {
        CacheStatusResponse.RedisInfo info = new CacheStatusResponse.RedisInfo();
        try {
            var conn = redisTemplate.getConnectionFactory().getConnection();
            Properties props = conn.serverCommands().info();

            if (props != null) {
                info.setVersion(props.getProperty("redis_version", ""));
                info.setUsedMemory(props.getProperty("used_memory_human", ""));
                info.setConnectedClients(props.getProperty("connected_clients", ""));
                info.setUptimeDays(props.getProperty("uptime_in_days", ""));
            }

            Long dbSize = conn.serverCommands().dbSize();
            info.setTotalKeys(dbSize != null ? String.valueOf(dbSize) : "0");
        } catch (Exception e) {
            log.warn("Failed to get Redis info: {}", e.getMessage());
        }
        return info;
    }

    private List<CacheStatusResponse.CacheEntry> scanCacheEntries(String pattern) {
        List<CacheStatusResponse.CacheEntry> entries = new ArrayList<>();
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys == null) return entries;

            for (String key : keys) {
                CacheStatusResponse.CacheEntry entry = new CacheStatusResponse.CacheEntry();
                entry.setKey(key);

                Long ttl = redisTemplate.getExpire(key);
                entry.setTtlSeconds(ttl != null && ttl > 0 ? ttl : null);

                String value = redisTemplate.opsForValue().get(key);
                // 脱敏：session 类的值只显示前 8 位
                if (key.contains("session")) {
                    entry.setValue(value != null ? value.substring(0, Math.min(8, value.length())) + "..." : null);
                } else {
                    entry.setValue(value);
                }

                entries.add(entry);
            }
        } catch (Exception e) {
            log.warn("Failed to scan cache entries: {}", e.getMessage());
        }
        return entries;
    }
}
