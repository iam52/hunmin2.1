package com.hunmin.domain.notification.dto;

import com.hunmin.domain.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSendDTO {
    private Long notificationId;
    private Long memberId;
    private NotificationType notificationType;
    private String message;
    private String url;
}
