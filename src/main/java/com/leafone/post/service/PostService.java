package com.leafone.post.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafone.auth.mapper.UserMapper;
import com.leafone.auth.model.User;
import com.leafone.common.exception.BizException;
import com.leafone.common.response.PageResult;
import com.leafone.post.mapper.HomeHeadlineMapper;
import com.leafone.post.mapper.PostAttachmentMapper;
import com.leafone.post.mapper.PostMapper;
import com.leafone.post.model.HomeHeadline;
import com.leafone.post.model.Post;
import com.leafone.post.model.PostAttachment;
import com.leafone.post.service.dto.*;
import com.leafone.reaction.mapper.ReactionMapper;
import com.leafone.reaction.model.Reaction;
import com.leafone.user.mapper.UserFollowMapper;
import com.leafone.user.model.UserFollow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostAttachmentMapper postAttachmentMapper;
    private final HomeHeadlineMapper homeHeadlineMapper;
    private final ReactionMapper reactionMapper;
    private final UserMapper userMapper;
    private final UserFollowMapper userFollowMapper;

    public HomeResponse home() {
        LambdaQueryWrapper<HomeHeadline> hw = new LambdaQueryWrapper<>();
        hw.eq(HomeHeadline::getIsPinned, 1).or()
          .isNotNull(HomeHeadline::getPublishedAt)
          .orderByDesc(HomeHeadline::getIsPinned)
          .orderByDesc(HomeHeadline::getPublishedAt)
          .last("LIMIT 10");
        List<HomeHeadline> headlines = homeHeadlineMapper.selectList(hw);

        LambdaQueryWrapper<Post> pw = new LambdaQueryWrapper<>();
        pw.eq(Post::getStatus, 1)
          .orderByDesc(Post::getPublishedAt)
          .last("LIMIT 10");
        List<Post> latestPosts = postMapper.selectList(pw);

        HomeResponse resp = new HomeResponse();
        resp.setHeadlines(headlines);
        resp.setLatestPosts(latestPosts);
        return resp;
    }

    public PageResult<Post> postList(Long moduleId, String tab, String feed,
                                     String keyword, int page, int pageSize, Long userId) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1);

        if (moduleId != null) wrapper.eq(Post::getModuleId, moduleId);
        if (keyword != null && !keyword.isBlank()) wrapper.like(Post::getTitle, keyword);
        if ("featured".equals(tab)) wrapper.eq(Post::getIsFeatured, 1);

        switch (feed) {
            case "following" -> {
                if (userId == null) {
                    return PageResult.of(List.of(), page, pageSize, 0);
                }
                List<UserFollow> follows = userFollowMapper.selectList(
                        new LambdaQueryWrapper<UserFollow>()
                                .eq(UserFollow::getFollowerId, userId)
                                .select(UserFollow::getFollowingId));
                List<Long> followingIds = follows.stream().map(UserFollow::getFollowingId).toList();
                if (followingIds.isEmpty()) {
                    return PageResult.of(List.of(), page, pageSize, 0);
                }
                wrapper.in(Post::getAuthorId, followingIds);
                wrapper.orderByDesc(Post::getPublishedAt);
            }
            case "recommend" -> {
                wrapper.last("ORDER BY (like_count * 3 + comment_count * 2 + favorite_count * 4 + view_count * 0.1) * "
                        + "EXP(-0.05 * TIMESTAMPDIFF(HOUR, published_at, NOW())) DESC");
            }
            default -> wrapper.orderByDesc(Post::getPublishedAt);
        }

        Page<Post> pageResult = postMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(pageResult.getRecords(), page, pageSize, pageResult.getTotal());
    }

    public PostDetailResponse postDetail(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() != 1) {
            throw new BizException(40400, "Post not found");
        }
        postMapper.incrementViewCount(postId);
        post.setViewCount(post.getViewCount() + 1);

        PostDetailResponse resp = new PostDetailResponse();
        resp.setId(post.getId());
        resp.setAuthorId(post.getAuthorId());

        User author = userMapper.selectById(post.getAuthorId());
        if (author != null) {
            resp.setAuthorNickname(author.getNickname());
            resp.setAuthorAvatarUrl(author.getAvatarUrl());
        }

        resp.setModuleId(post.getModuleId());
        resp.setTitle(post.getTitle());
        resp.setSummary(post.getSummary());
        resp.setContent(post.getContent());
        resp.setCoverUrl(post.getCoverUrl());
        resp.setCoverType(post.getCoverType());
        resp.setPrice(post.getPrice());
        resp.setLocationName(post.getLocationName());
        resp.setViewCount(post.getViewCount());
        resp.setShareCount(post.getShareCount());
        resp.setCommentCount(post.getCommentCount());
        resp.setLikeCount(post.getLikeCount());
        resp.setFavoriteCount(post.getFavoriteCount());

        if (userId != null) {
            LambdaQueryWrapper<Reaction> reactionQ = new LambdaQueryWrapper<>();
            reactionQ.eq(Reaction::getUserId, userId)
                     .eq(Reaction::getTargetType, "POST")
                     .eq(Reaction::getTargetId, postId)
                     .in(Reaction::getReactionType, "LIKE", "FAVORITE")
                     .isNull(Reaction::getDeletedAt);
            List<Reaction> reactions = reactionMapper.selectList(reactionQ);
            resp.setLiked(reactions.stream().anyMatch(r -> "LIKE".equals(r.getReactionType())));
            resp.setFavorited(reactions.stream().anyMatch(r -> "FAVORITE".equals(r.getReactionType())));
        }

        List<PostAttachment> attachments = postAttachmentMapper.selectList(
                new LambdaQueryWrapper<PostAttachment>()
                        .eq(PostAttachment::getPostId, postId)
                        .orderByAsc(PostAttachment::getSortOrder));
        resp.setAttachments(attachments.stream().map(att -> {
            AttachmentResponse ar = new AttachmentResponse();
            ar.setId(att.getId());
            ar.setDisplayName(att.getDisplayName());
            ar.setFileUrl(att.getFileUrl());
            ar.setFileSize(att.getFileSize());
            ar.setFileType(att.getFileType());
            return ar;
        }).toList());

        return resp;
    }

    @Transactional(rollbackFor = Exception.class)
    public Post createPost(PostCreateRequest request, Long userId) {
        Post post = new Post();
        post.setAuthorId(userId);
        post.setModuleId(request.getModuleId());
        post.setTitle(request.getTitle());
        post.setSummary(request.getSummary());
        post.setContent(request.getContent());
        post.setCoverUrl(request.getCoverUrl());
        post.setLocationName(request.getLocationName());
        post.setStatus(1);
        post.setViewCount(0);
        post.setShareCount(0);
        post.setCommentCount(0);
        post.setLikeCount(0);
        post.setFavoriteCount(0);
        post.setPublishedAt(LocalDateTime.now());
        postMapper.insert(post);

        if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
            for (int i = 0; i < request.getAttachments().size(); i++) {
                var req = request.getAttachments().get(i);
                PostAttachment att = new PostAttachment();
                att.setPostId(post.getId());
                att.setDisplayName(req.getDisplayName());
                att.setFileUrl(req.getFileUrl());
                att.setFileSize(req.getFileSize());
                att.setFileType(req.getFileType());
                att.setSortOrder(i);
                postAttachmentMapper.insert(att);
            }
        }

        return post;
    }

    public void sharePost(Long postId) {
        postMapper.incrementShareCount(postId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Post updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BizException(40400, "Post not found");
        if (!post.getAuthorId().equals(userId)) throw new BizException(40300, "No permission to edit this post");

        if (request.getModuleId() != null) post.setModuleId(request.getModuleId());
        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getSummary() != null) post.setSummary(request.getSummary());
        if (request.getContent() != null) post.setContent(request.getContent());
        if (request.getCoverUrl() != null) post.setCoverUrl(request.getCoverUrl());
        if (request.getLocationName() != null) post.setLocationName(request.getLocationName());
        postMapper.updateById(post);

        if (request.getAttachments() != null) {
            postAttachmentMapper.delete(new LambdaQueryWrapper<PostAttachment>()
                    .eq(PostAttachment::getPostId, postId));
            for (int i = 0; i < request.getAttachments().size(); i++) {
                var req = request.getAttachments().get(i);
                PostAttachment att = new PostAttachment();
                att.setPostId(postId);
                att.setDisplayName(req.getDisplayName());
                att.setFileUrl(req.getFileUrl());
                att.setFileSize(req.getFileSize());
                att.setFileType(req.getFileType());
                att.setSortOrder(i);
                postAttachmentMapper.insert(att);
            }
        }

        return post;
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) throw new BizException(40400, "Post not found");
        if (!post.getAuthorId().equals(userId)) throw new BizException(40300, "No permission to delete this post");

        post.setStatus(3);
        postMapper.updateById(post);
    }
}
