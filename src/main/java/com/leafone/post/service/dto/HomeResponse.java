package com.leafone.post.service.dto;

import com.leafone.post.model.HomeHeadline;
import com.leafone.post.model.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "首页响应")
public class HomeResponse {
    @Schema(description = "头条列表")
    private List<HomeHeadline> headlines;
    @Schema(description = "最新帖子列表")
    private List<Post> latestPosts;
}
