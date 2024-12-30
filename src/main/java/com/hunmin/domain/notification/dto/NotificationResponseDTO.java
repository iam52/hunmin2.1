package com.hunmin.domain.notification.dto;

import com.hunmin.domain.notification.entity.Notification;
import com.hunmin.domain.notification.entity.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotificationResponseDTO {
    private Long notificationId;
    private String message;
    private NotificationType notificationType;
    private String url;
    private Boolean isRead;

    public NotificationResponseDTO(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.message = notification.getMessage();
        this.notificationType = notification.getNotificationType();
        this.url = notification.getUrl();
        this.isRead = notification.getIsRead();
    }
}
