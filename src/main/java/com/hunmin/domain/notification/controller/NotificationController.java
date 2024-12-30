package com.hunmin.domain.notification.controller;

import com.hunmin.domain.notification.dto.NotificationResponseDTO;
import com.hunmin.domain.notification.entity.Notification;
import com.hunmin.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
@Tag(name = "알림", description = "알림 CRUD")
public class NotificationController {
    private final NotificationService notificationService;

    //sse 연결
    @GetMapping(value = "/subscribe/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "sse 연결", description = "sse 연결할 때 사용하는 API")
    public ResponseEntity<SseEmitter> subscribe(@PathVariable Long memberId) {
        return new ResponseEntity<>(notificationService.subscribe(memberId), HttpStatus.OK);
    }

    //회원 별 알림 조회
    @GetMapping("/{memberId}")
    @Operation(summary = "알림 조회", description = "회원 별 알림을 조회할 때 사용하는 API")
    public ResponseEntity<List<NotificationResponseDTO>> readNotifications(@PathVariable Long memberId) {
        List<Notification> notifications = notificationService.ReadNotificationByMember(memberId);
        List<NotificationResponseDTO> notificationResponseDTOS = notifications.stream()
                .map(NotificationResponseDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(notificationResponseDTOS, HttpStatus.OK);
    }

    //알림 읽음
    @PutMapping("/{notificationId}")
    @Operation(summary = "알림 읽음", description = "알림을 읽음으로 수정할 때 사용하는 API")
    public ResponseEntity<NotificationResponseDTO> updateNotifications(@PathVariable Long notificationId) {
        return ResponseEntity.ok(notificationService.updateNotification(notificationId));
    }
}