package com.leafone.message.event;

import com.leafone.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final MessageService messageService;

    @EventListener
    public void onNotification(NotificationEvent event) {
        messageService.createMessage(
                event.getUserId(), event.getSenderId(), event.getType(),
                event.getTitle(), event.getContent(),
                event.getTargetType(), event.getTargetId());
    }
}
