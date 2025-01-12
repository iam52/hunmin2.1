package com.hunmin.domain.follow.service;

import com.hunmin.domain.follow.dto.FollowRequestDTO;
import com.hunmin.domain.notification.dto.NotificationSendDTO;
import com.hunmin.global.common.PageRequestDTO;
import com.hunmin.domain.follow.entity.Follow;
import com.hunmin.domain.follow.entity.FollowStatus;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.notification.entity.NotificationType;
import com.hunmin.domain.notification.service.NotificationService;
import com.hunmin.global.handler.SseEmitters;
import com.hunmin.domain.follow.repository.FollowRepository;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
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
            Member followee = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
            Member owner = memberRepository.findByEmail(myEmail).get();

            if (followee.getMemberId().equals(owner.getMemberId())) {
                throw ErrorCode.FOLLOW_CREATE_FAIL.throwException();
            }

            // 중복체크
            Optional<Follow> foundMember = followRepository.findByMemberId(owner.getMemberId(), memberId);
            if (foundMember.isPresent()) {
                throw ErrorCode.FOLLOW_ALREADY_EXIST.throwException();
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
            Member followee = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
            Member owner = memberRepository.findByEmail(myEmail).get();

            // 중복체크
            Optional<Follow> foundMember = followRepository.findByMemberId(owner.getMemberId(),memberId);
            if (foundMember.isPresent()) {
                throw ErrorCode.FOLLOW_ALREADY_EXIST.throwException();
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
            Member owner = memberRepository.findByEmail(myEmail).get();
            Follow foundMember = followRepository.findByMemberId(owner.getMemberId(), memberId)
                    .orElseThrow(ErrorCode.FOLLOW_NOT_FOUND::throwException);

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
            Member member = memberRepository.findByEmail(email).get();
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
            Member owner = memberRepository.findByEmail(myEmail).get();
            Follow foundMember = followRepository.findByMemberId(memberId,owner.getMemberId())
                    .orElseThrow(ErrorCode.FOLLOW_NOT_FOUND::throwException);

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
            Member owner = memberRepository.findByEmail(myEmail).get();
            Follow foundMember = followRepository.findByMemberId(memberId,owner.getMemberId())
                    .orElseThrow(ErrorCode.FOLLOW_NOT_FOUND::throwException);
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
