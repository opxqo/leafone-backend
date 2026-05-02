package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "创建/编辑头条请求")
public class HeadlineCreateRequest {
    @NotBlank
    @Schema(description = "标题", example = "校园歌手大赛报名开始")
    private String title;
    @Schema(description = "摘要")
    private String summary;
    @Schema(description = "封面图URL")
    private String coverUrl;
    @Schema(description = "关联目标类型", example = "POST")
    private String targetType;
    @Schema(description = "关联目标ID")
    private Long targetId;
    @Schema(description = "外部链接URL")
    private String externalUrl;
    @Schema(description = "是否置顶（0=否, 1=是）", example = "0")
    private Integer isPinned;
}
