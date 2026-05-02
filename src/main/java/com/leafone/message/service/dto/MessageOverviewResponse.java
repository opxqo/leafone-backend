package com.leafone.message.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "消息概览响应")
public class MessageOverviewResponse {
    @Schema(description = "未读消息总数", example = "5")
    private long unreadTotal;
}
