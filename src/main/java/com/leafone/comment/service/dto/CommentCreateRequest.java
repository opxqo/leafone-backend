package com.leafone.comment.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "发表评论请求")
public class CommentCreateRequest {

    @NotBlank
    @Size(max = 1000)
    @Schema(description = "评论内容", example = "说得好！")
    private String content;

    @Schema(description = "父评论ID（回复时传入）")
    private Long parentId;

    @Schema(description = "被回复用户ID")
    private Long replyToUserId;
}
