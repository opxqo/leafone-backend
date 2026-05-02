package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建/编辑版块请求")
public class ModuleCreateRequest {
    @NotBlank
    @Schema(description = "版块编码", example = "trade")
    private String code;
    @NotBlank
    @Schema(description = "版块名称", example = "二手交易")
    private String name;
    @Schema(description = "版块描述")
    private String description;
    @Schema(description = "图标标识", example = "icon-trade")
    private String iconKey;
    @Schema(description = "排序权重", example = "0")
    private Integer sortOrder;
    @Schema(description = "是否启用（0=否, 1=是）", example = "1")
    private Integer enabled;
}
