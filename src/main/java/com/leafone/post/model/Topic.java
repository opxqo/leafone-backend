package com.leafone.post.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("topics")
@Schema(description = "话题")
public class Topic {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "话题ID")
    private Long id;
    @Schema(description = "话题名称", example = "毕业季")
    private String name;
    @Schema(description = "话题描述")
    private String description;
    @Schema(description = "热度分数")
    private Integer heatScore;
    @Schema(description = "帖子数量")
    private Integer postCount;
    @Schema(description = "是否启用（0=否, 1=是）")
    private Integer enabled;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
