package com.leafone.message.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leafone.common.exception.BizException;
import com.leafone.common.response.PageResult;
import com.leafone.message.mapper.MessageMapper;
import com.leafone.message.model.Message;
import com.leafone.message.service.dto.MessageOverviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageMapper messageMapper;

    public MessageOverviewResponse messageOverview(Long userId) {
        long unreadTotal = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getUserId, userId)
                        .isNull(Message::getReadAt));

        MessageOverviewResponse resp = new MessageOverviewResponse();
        resp.setUnreadTotal(unreadTotal);
        return resp;
    }

    public PageResult<Message> messageList(Long userId, String type, int page, int pageSize) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId);

        if (!"all".equals(type)) {
            wrapper.eq(Message::getType, type);
        }
        wrapper.orderByDesc(Message::getCreatedAt);

        Page<Message> pageResult = messageMapper.selectPage(new Page<>(page, pageSize), wrapper);
        return PageResult.of(pageResult.getRecords(), page, pageSize, pageResult.getTotal());
    }

    public void readAll(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId).isNull(Message::getReadAt);

        Message update = new Message();
        update.setReadAt(LocalDateTime.now());
        messageMapper.update(update, wrapper);
    }

    public void markRead(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null) return;
        if (!message.getUserId().equals(userId)) throw new BizException(40300, "无权操作此消息");
        message.setReadAt(LocalDateTime.now());
        messageMapper.updateById(message);
    }
}
