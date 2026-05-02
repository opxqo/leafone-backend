package com.leafone.profile.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "72小时用电量响应")
public class DormPowerHourlyResponse {

    @Schema(description = "寝室房间号", example = "南1-548")
    private String roomName;

    @Schema(description = "小时用电量列表（最近72小时，每小时一个数据点）")
    private List<BigDecimal> usage;
}
