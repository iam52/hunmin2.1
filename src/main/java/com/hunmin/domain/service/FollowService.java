package com.hunmin.domain.service;

import com.hunmin.domain.dto.follow.FollowRequestDTO;
import com.hunmin.domain.dto.notification.NotificationSendDTO;
import com.hunmin.domain.dto.page.PageRequestDTO;
import com.hunmin.domain.entity.Follow;
import com.hunmin.domain.entity.FollowStatus;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.NotificationType;
import com.hunmin.domain.handler.SseEmitters;
import com.hunmin.domain.repository.FollowRepository;
import com.hunmin.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    // 팔로워 등록
    public FollowRequestDTO register(String myEmail, Long memberId) {
        try {
            Member followee = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
            Member owner = memberRepository.findByEmail(myEmail);

            if (followee.getMemberId().equals(owner.getMemberId())) {
                throw FollowException.IMPOSSIBLE_FOLLOW.get();
            }

            // 중복체크
            Optional<Follow> foundMember = followRepository.findByMemberId(owner.getMemberId(), memberId);
            if (foundMember.isPresent()) {
                throw FollowException.DUPLICATED_FOLLOW.get();
            }

            Follow follow = Follow.builder()
                    .follower(owner)
                    .followee(followee)
                    .isBlock(false)
                    .notification(true)
                    .status(FollowStatus.PENDING)
                    .build();

            // 알림
            Long senderId = owner.getMemberId();
            Long receiverId = followee.getMemberId();

            if (!receiverId.equals(senderId)) {
                NotificationSendDTO notificationSendDTO = NotificationSendDTO.builder()
                        .memberId(receiverId)
                        .message(owner.getNickname() + "님이 팔로우 요청을 보냈습니다.")
                        .notificationType(NotificationType.FOLLOW)
                        .url("/follow")
                        .build();
                notificationService.send(notificationSendDTO);

                String emitterId = receiverId + "_";
                SseEmitter emitter = sseEmitters.findSingleEmitter(emitterId);


                if (emitter != null) {
                    try {
                        emitter.send(notificationSendDTO);
                    } catch (IOException e) {
                        log.error("Error sending comment to client via SSE: {}", e.getMessage());
                        sseEmitters.delete(emitterId);
                    }
                }
            }
            return new FollowRequestDTO(followRepository.save(follow));

        } catch (RuntimeException e) {
            log.error("팔로우 등록 실패{}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    // 팔로이 수락
    public FollowRequestDTO registerAccept(String myEmail, Long memberId) {
        try {
            Member followee = memberRepository.findById(memberId).orElseThrow(MemberException.NOT_FOUND::get);
            Member owner = memberRepository.findByEmail(myEmail);

            // 중복체크
            Optional<Follow> foundMember = followRepository.findByMemberId(owner.getMemberId(),memberId);
            if (foundMember.isPresent()) {
                throw FollowException.DUPLICATED_FOLLOW.get();
            }

            Follow follow = Follow.builder()
                    .follower(owner)
                    .followee(followee)
                    .isBlock(false)
                    .status(FollowStatus.ACCEPTED)
                    .notification(true)
                    .build();
            followRepository.save(follow);
            Follow follower = followRepository.findByMemberId(memberId,owner.getMemberId()).get();
            follower.setStatus(FollowStatus.ACCEPTED);
            followRepository.save(follower);

            return new FollowRequestDTO(followRepository.save(follow));
        } catch (RuntimeException e) {
            log.error("팔로우 수락 실패{}", e.getMessage());
            throw e;
        }
    }

    // 팔로이 삭제
    public Boolean remove(String myEmail, Long memberId) {
        try {
            Member owner = memberRepository.findByEmail(myEmail);
            Follow foundMember = followRepository.findByMemberId(owner.getMemberId(), memberId)
                    .orElseThrow(FollowException.NOT_FOUND::get);

            followRepository.deleteById(foundMember.getFollowId());

            return true;
        } catch (RuntimeException e) {
            log.error("팔로이 삭제 실패 {}", e.getMessage());
            throw e;
        }
    }

    // 팔로이 리스트 조회
    public Page<FollowRequestDTO> readPage(PageRequestDTO pageRequestDTO, String email) {
        try {
            Member member = memberRepository.findByEmail(email);
            Sort sort = Sort.by("followId").descending();
            Pageable pageable = pageRequestDTO.getPageable(sort);
            return followRepository.getFollowPage(member.getMemberId(), pageable);
        } catch (RuntimeException e) {
            log.error("페이징 실패 {}", e.getMessage());
            throw e;
        }
    }

    // 알림 변경
    @Transactional
    public Boolean turnNotification(String myEmail, Long memberId) {
        try {
            Member owner = memberRepository.findByEmail(myEmail);
            Follow foundMember = followRepository.findByMemberId(memberId,owner.getMemberId())
                    .orElseThrow(FollowException.NOT_FOUND::get);

            log.info("B owner {}",owner);
            log.info("B memberId {}",memberId);
            log.info("B foundMember {}",foundMember);
            foundMember.setNotification(!foundMember.getNotification());
            return true;

        } catch (RuntimeException e) {
            log.error("알림 변경에 실패하였습니다. {}", e.getMessage());
            throw e;
        }
    }

    // 차단 상태 변경
    @Transactional
    public Boolean blockFollower(String myEmail, Long memberId) {
        try {
            Member owner = memberRepository.findByEmail(myEmail);
            Follow foundMember = followRepository.findByMemberId(memberId,owner.getMemberId())
                    .orElseThrow(FollowException.NOT_FOUND::get);
            log.info("A owner {}",owner);
            log.info("A memberId {}",memberId);
            log.info("A foundMember {}",foundMember);
            foundMember.setNotification(foundMember.getIsBlock());
            foundMember.setIsBlock(!foundMember.getIsBlock());
            return true;
        } catch (RuntimeException e) {
            log.error("상대방 차단에 실패하였습니다. {}", e.getMessage());
            throw e;
        }
    }

    public boolean isFollowing(Long memberId, Long followeeId) {
        return followRepository.existsByFollower_MemberIdAndFollowee_MemberId(memberId, followeeId);
    }

}
