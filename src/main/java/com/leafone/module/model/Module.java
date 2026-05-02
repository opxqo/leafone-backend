package com.leafone.module.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("modules")
@Schema(description = "论坛版块")
public class Module {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "版块ID")
    private Long id;
    @Schema(description = "版块编码", example = "trade")
    private String code;
    @Schema(description = "版块名称", example = "二手交易")
    private String name;
    @Schema(description = "版块描述")
    private String description;
    @Schema(description = "图标标识")
    private String iconKey;
    @Schema(description = "排序权重")
    private Integer sortOrder;
    @Schema(description = "今日帖子数")
    private Integer postCountToday;
    @Schema(description = "是否启用（0=否, 1=是）")
    private Integer enabled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
