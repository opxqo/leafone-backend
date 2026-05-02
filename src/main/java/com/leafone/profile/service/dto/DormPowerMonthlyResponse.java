package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "寝室月用电量统计")
public class DormPowerMonthlyResponse {

    @Schema(description = "房间号", example = "南1-103")
    private String roomName;

    @Schema(description = "月份列表", example = "[\"2025-11\", \"2025-12\", \"2026-01\", \"2026-02\", \"2026-03\", \"2026-04\"]")
    private List<String> months;

    @Schema(description = "每月用电量（度）", example = "[74.97, 97.25, 0.03, 73.68, 63.31, 2.41]")
    private List<BigDecimal> usage;
}
