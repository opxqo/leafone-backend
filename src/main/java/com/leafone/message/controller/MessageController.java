package com.leafone.message.controller;

import com.leafone.common.response.PageResult;
import com.leafone.common.util.AuthUtil;
import com.leafone.common.response.R;
import com.leafone.message.model.Message;
import com.leafone.message.service.MessageService;
import com.leafone.message.service.dto.MessageOverviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息通知", description = "用户消息通知的查看和已读标记")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "消息概览", description = "获取未读消息总数，需登录")
    @GetMapping("/messages/overview")
    public R<MessageOverviewResponse> messageOverview(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        return R.ok(messageService.messageOverview(userId));
    }

    @Operation(summary = "消息列表", description = "分页获取消息通知列表，支持按类型筛选，需登录")
    @GetMapping("/messages")
    public R<PageResult<Message>> messageList(
            @Parameter(description = "消息类型（all=全部）") @RequestParam(required = false, defaultValue = "all") String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") int pageSize,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        pageSize = PageResult.capPageSize(pageSize);
        return R.ok(messageService.messageList(userId, type, page, pageSize));
    }

    @Operation(summary = "全部已读", description = "将所有未读消息标记为已读，需登录")
    @PostMapping("/messages/read-all")
    public R<Void> readAll(@AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        messageService.readAll(userId);
        return R.ok();
    }

    @Operation(summary = "标记已读", description = "将单条消息标记为已读，需登录")
    @PostMapping("/messages/{messageId}/read")
    public R<Void> markRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            @AuthenticationPrincipal Long userId) {
        AuthUtil.requireLogin(userId);
        messageService.markRead(messageId, userId);
        return R.ok();
    }
}
