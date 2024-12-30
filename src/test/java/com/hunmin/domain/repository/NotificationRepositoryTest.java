package com.hunmin.domain.repository;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.notification.entity.Notification;
import com.hunmin.domain.notification.entity.NotificationType;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.domain.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class NotificationRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // 회원 별 알림 조회 테스트
    @Test
    public void testFindByMemberId() {
        Member member = memberRepository.findById(1L).get();

        Notification notification1 = Notification.builder()
                .member(member)
                .message("첫 번째 알림 테스트")
                .notificationType(NotificationType.COMMENT)
                .url("/board/1")
                .isRead(false)
                .build();

        Notification notification2 = Notification.builder()
                .member(member)
                .message("두 번째 알림 테스트")
                .notificationType(NotificationType.COMMENT)
                .url("/board/2")
                .isRead(false)
                .build();

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);

        List<Notification> notifications = notificationRepository.findByMemberId(member.getMemberId());

        assertNotNull(notifications);
        assertEquals(2, notifications.size());

        assertEquals("두 번째 알림 테스트", notifications.get(0).getMessage());
        assertEquals("첫 번째 알림 테스트", notifications.get(1).getMessage());
    }
}
