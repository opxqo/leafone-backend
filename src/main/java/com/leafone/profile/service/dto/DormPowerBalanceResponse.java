package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "寝室电费余额")
public class DormPowerBalanceResponse {

    @Schema(description = "房间号", example = "南1-103")
    private String roomName;

    @Schema(description = "基本账户余额（元）", example = "5.23")
    private BigDecimal basicBalance;

    @Schema(description = "补助账户余额（元）", example = "0.00")
    private BigDecimal subsidyBalance;
}
