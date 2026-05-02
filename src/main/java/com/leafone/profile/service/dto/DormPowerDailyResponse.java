package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "寝室日用电量统计")
public class DormPowerDailyResponse {

    @Schema(description = "房间号", example = "南1-103")
    private String roomName;

    @Schema(description = "日期列表", example = "[\"04-25\", \"04-26\", \"04-27\", \"04-28\", \"04-29\", \"04-30\", \"05-01\"]")
    private List<String> dates;

    @Schema(description = "每日用电量（度）", example = "[3.21, 2.12, 2.25, 2.32, 1.23, 1.95, 2.26]")
    private List<BigDecimal> usage;
}
