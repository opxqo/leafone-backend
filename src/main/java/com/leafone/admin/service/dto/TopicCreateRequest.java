package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建/编辑话题请求")
public class TopicCreateRequest {
    @NotBlank
    @Schema(description = "话题名称", example = "毕业季")
    private String name;
    @Schema(description = "话题描述")
    private String description;
    @Schema(description = "热度分数", example = "100")
    private Integer heatScore;
    @Schema(description = "是否启用（0=否, 1=是）", example = "1")
    private Integer enabled;
}
