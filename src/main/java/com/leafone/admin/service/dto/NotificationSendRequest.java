package com.leafone.admin.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理员发送通知请求")
public class NotificationSendRequest {
    @Schema(description = "接收用户ID列表（为空则发送给全体用户）")
    private List<Long> userIds;

    @NotBlank
    @Schema(description = "消息标题", example = "系统维护通知")
    private String title;

    @NotBlank
    @Schema(description = "消息内容", example = "系统将于今晚22:00进行维护升级")
    private String content;

    @Schema(description = "关联目标类型（POST/URL/null）")
    private String targetType;

    @Schema(description = "关联目标ID")
    private Long targetId;
}
