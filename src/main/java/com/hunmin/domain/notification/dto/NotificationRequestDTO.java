package com.hunmin.domain.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDTO {
    @NotBlank
    private Long notificationId;

    @NotBlank
    private Long memberId;
}
