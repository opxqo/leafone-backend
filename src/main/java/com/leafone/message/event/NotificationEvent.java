package com.leafone.message.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NotificationEvent extends ApplicationEvent {

    private final Long userId;
    private final Long senderId;
    private final String type;
    private final String title;
    private final String content;
    private final String targetType;
    private final Long targetId;

    public NotificationEvent(Object source, Long userId, Long senderId, String type,
                             String title, String content, String targetType, Long targetId) {
        super(source);
        this.userId = userId;
        this.senderId = senderId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.targetType = targetType;
        this.targetId = targetId;
    }
}
