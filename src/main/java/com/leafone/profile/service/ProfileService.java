package com.leafone.profile.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafone.auth.mapper.UserMapper;
import com.leafone.auth.model.User;
import com.leafone.common.exception.BizException;
import com.leafone.common.response.PageResult;
import com.leafone.post.mapper.PostMapper;
import com.leafone.post.model.Post;
import com.leafone.profile.mapper.FeedbackMapper;
import com.leafone.profile.model.Feedback;
import com.leafone.profile.service.dto.*;
import com.leafone.reaction.mapper.ReactionMapper;
import com.leafone.reaction.model.Reaction;
import com.leafone.user.mapper.StudentProfileMapper;
import com.leafone.user.mapper.UserFollowMapper;
import com.leafone.user.model.StudentProfile;
import com.leafone.user.model.UserFollow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final PostMapper postMapper;
    private final ReactionMapper reactionMapper;
    private final FeedbackMapper feedbackMapper;
    private final UserFollowMapper userFollowMapper;

    public ProfileResponse myProfile(Long userId) {
        User user = userMapper.selectById(userId);
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>().eq(StudentProfile::getUserId, userId));

        ProfileResponse resp = new ProfileResponse();
        resp.setUser(user);
        resp.setStudentProfile(profile);
        return resp;
    }

    public void updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BizException(40400, "User not found");

        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDorm() != null) user.setDorm(request.getDorm());

        userMapper.updateById(user);
    }

    public PageResult<Post> myFavorites(Long userId, int page, int pageSize) {
        Page<Reaction> favPage = reactionMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Reaction>()
                        .eq(Reaction::getUserId, userId)
                        .eq(Reaction::getTargetType, "POST")
                        .eq(Reaction::getReactionType, "FAVORITE")
                        .isNull(Reaction::getDeletedAt)
                        .orderByDesc(Reaction::getCreatedAt));

        List<Long> postIds = favPage.getRecords().stream()
                .map(Reaction::getTargetId)
                .toList();
        List<Post> posts = postIds.isEmpty()
                ? List.of()
                : postMapper.selectBatchIds(postIds).stream()
                    .filter(p -> p.getStatus() == 1)
                    .toList();

        return PageResult.of(posts, page, pageSize, favPage.getTotal());
    }

    public PageResult<Post> myPosts(Long userId, int page, int pageSize) {
        Page<Post> pageResult = postMapper.selectPage(new Page<>(page, pageSize),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getAuthorId, userId)
                        .orderByDesc(Post::getCreatedAt));

        return PageResult.of(pageResult.getRecords(), page, pageSize, pageResult.getTotal());
    }

    public void submitFeedback(Long userId, FeedbackCreateRequest request) {
        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setContent(request.getContent());
        feedback.setContact(request.getContact());
        feedback.setStatus("PENDING");
        feedbackMapper.insert(feedback);
    }

    public UserPublicProfileResponse userProfile(Long targetUserId, Long currentUserId) {
        User user = userMapper.selectById(targetUserId);
        if (user == null) throw new BizException(40400, "User not found");

        UserPublicProfileResponse resp = new UserPublicProfileResponse();
        resp.setUserId(user.getId());
        resp.setNickname(user.getNickname());
        resp.setAvatarUrl(user.getAvatarUrl());
        resp.setGender(user.getGender());

        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>()
                        .eq(StudentProfile::getUserId, targetUserId)
                        .eq(StudentProfile::getVerified, 1));
        resp.setStudentProfile(profile);

        resp.setPostCount(postMapper.selectCount(
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getAuthorId, targetUserId)
                        .eq(Post::getStatus, 1)));

        resp.setFollowerCount(userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowingId, targetUserId)
                        .isNull(UserFollow::getDeletedAt)));

        resp.setFollowingCount(userFollowMapper.selectCount(
                new LambdaQueryWrapper<UserFollow>()
                        .eq(UserFollow::getFollowerId, targetUserId)
                        .isNull(UserFollow::getDeletedAt)));

        if (currentUserId != null && !currentUserId.equals(targetUserId)) {
            resp.setFollowed(userFollowMapper.selectCount(
                    new LambdaQueryWrapper<UserFollow>()
                            .eq(UserFollow::getFollowerId, currentUserId)
                            .eq(UserFollow::getFollowingId, targetUserId)
                            .isNull(UserFollow::getDeletedAt)) > 0);
        } else {
            resp.setFollowed(false);
        }

        return resp;
    }

    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new BizException(40000, "Cannot follow yourself");
        }
        User target = userMapper.selectById(followingId);
        if (target == null) throw new BizException(40400, "User not found");

        LambdaQueryWrapper<UserFollow> check = new LambdaQueryWrapper<>();
        check.eq(UserFollow::getFollowerId, followerId)
             .eq(UserFollow::getFollowingId, followingId)
             .isNull(UserFollow::getDeletedAt);
        if (userFollowMapper.selectCount(check) > 0) {
            throw new BizException(40900, "Already followed");
        }

        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowMapper.insert(follow);
    }

    public void unfollow(Long followerId, Long followingId) {
        LambdaQueryWrapper<UserFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollow::getFollowerId, followerId)
               .eq(UserFollow::getFollowingId, followingId)
               .isNull(UserFollow::getDeletedAt);
        UserFollow follow = userFollowMapper.selectOne(wrapper);
        if (follow == null) throw new BizException(40400, "Not following this user");

        userFollowMapper.deleteById(follow.getId());
    }
}
