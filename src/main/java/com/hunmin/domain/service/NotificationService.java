package com.hunmin.domain.service;

import com.hunmin.domain.dto.notification.NotificationResponseDTO;
import com.hunmin.domain.dto.notification.NotificationSendDTO;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.Notification;
import com.hunmin.domain.handler.SseEmitters;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.domain.repository.NotificationRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class NotificationService {
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;

    private static final long TIMEOUT = 5 * 60 * 1000L;

    // 클라이언트의 연결 구독
    public SseEmitter subscribe(Long memberId) {
        String existingId = memberId + "_";
        Map<String, SseEmitter> existingEmitters = sseEmitters.findEmitter(existingId);

        existingEmitters.forEach((key, emitter) -> {
            emitter.complete();
            sseEmitters.delete(key);
        });

        SseEmitter emitter = new SseEmitter(TIMEOUT);
        String sseId = memberId + "_" + System.currentTimeMillis();

        sseEmitters.create(sseId, emitter);

        Map<String, Object> testContent = new HashMap<>();
        testContent.put("content", "connected!");
        sendToClient(emitter, sseId, testContent);

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            emitter.complete();
            sseEmitters.delete(sseId);
        });

        emitter.onError(throwable -> {
            log.error("[sse] SseEmitters 파일 add 메서드 : {}", throwable.getMessage());
            emitter.complete();
            sseEmitters.delete(sseId);
        });

        emitter.onCompletion(() -> {
            log.info("onCompletion callback");
            sseEmitters.delete(sseId);
        });

        return emitter;
    }

    // 클라이언트에 데이터 전송
    private void sendToClient(SseEmitter emitter, String sseId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(sseId)
                    .data(data));
        } catch (IOException exception) {
            log.error("Error sending data to client: {}", exception.getMessage());
            sseEmitters.delete(sseId);
            throw ErrorCode.NOTIFICATION_SEND_FAIL.throwException();
        }
    }

    // 알림 전송
    @Transactional
    public void send(NotificationSendDTO notificationSendDTO) {
        try {
            Member member = memberRepository.findById(notificationSendDTO.getMemberId())
                    .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
            log.info("member123123{}",member);

            Notification notification = Notification.builder()
                    .member(member)
                    .message(notificationSendDTO.getMessage())
                    .notificationType(notificationSendDTO.getNotificationType())
                    .url(notificationSendDTO.getUrl())
                    .isRead(false)
                    .build();
            log.info("notification123123{}",notification.toString());
            notificationRepository.save(notification);
            log.info("notification123123save{}",notification);

            String memberId = member.getMemberId() + "_";
            Map<String, SseEmitter> emitters = sseEmitters.findEmitter(memberId);
            NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO(notification);
            log.info("emitters123123123{}",emitters);
            emitters.forEach((key, emitter) -> {
                sendToClient(emitter, memberId, notificationResponseDTO);
            });
        } catch (Exception e) {
            throw ErrorCode.NOTIFICATION_SEND_FAIL.throwException();
        }
    }

    // 회원 별 알림 조회
    public List<Notification> ReadNotificationByMember(Long memberId) {
        return notificationRepository.findByMemberId(memberId);
    }

    // 알림 읽음 처리
    @Transactional
    public NotificationResponseDTO updateNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(ErrorCode.NOTIFICATION_NOT_FOUND::throwException);
        try {
            notification.changeIsRead(true);

            return new NotificationResponseDTO(notification);
        } catch (Exception e) {
            throw ErrorCode.NOTIFICATION_UPDATE_FAIL.throwException();
        }
    }
}
