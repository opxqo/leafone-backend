package com.leafone.profile.model;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("dorm_power_records")
@Schema(description = "宿舍用电记录")
public class DormPowerRecord {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "记录ID")
    private Long id;
    @Schema(description = "用户ID")
    private Long userId;
    @Schema(description = "房间名称", example = "南区1栋305")
    private String roomName;
    @Schema(description = "记录时间")
    private LocalDateTime recordTime;
    @Schema(description = "用电量（千瓦时）", example = "12.5")
    private BigDecimal powerKwh;
    @Schema(description = "记录类型", example = "DAILY")
    private String recordType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
