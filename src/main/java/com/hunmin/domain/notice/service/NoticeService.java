package com.hunmin.domain.notice.service;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.domain.notice.entity.Notice;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.domain.notice.dto.NoticePageRequestDTO;
import com.hunmin.domain.notice.dto.NoticeRequestDTO;
import com.hunmin.domain.notice.dto.NoticeResponseDTO;
import com.hunmin.domain.notice.dto.NoticeUpdateDTO;
import com.hunmin.domain.notice.repository.NoticeRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    //공지사항 리스트 조회
    public Page<NoticeResponseDTO> getAllNotices(NoticePageRequestDTO pageRequestDTO){
        try {
            Sort sort = Sort.by("noticeId").descending();
            Pageable pageable = pageRequestDTO.getPageable(sort);
            Page<Notice> noticePage = noticeRepository.findAllNoticesResponse(pageable);
            List<NoticeResponseDTO> responseDTOs = new ArrayList<>();
            // Notice를 NoticeResponseDTO로 변환
            return noticePage.map(NoticeResponseDTO::new);
        }catch (Exception e){
            log.error("getAllNotices error: {}",  e.getMessage());
            throw ErrorCode.NOTICE_NOT_FOUND.throwException();
        }
    }

    //공지사항 조회
    public NoticeResponseDTO getNoticeById(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(ErrorCode.NOTICE_NOT_FOUND::throwException);
        return new NoticeResponseDTO(notice);
    }

    //공지사항 등록
    public NoticeResponseDTO createNotice(NoticeRequestDTO noticeRequestDTO, String username){
        Member member = getMember(username);

        //관리자 아닐경우 예외 발생
        if (!member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw new AccessDeniedException("공지사항 접근 권한이 없습니다.");
        }
        Notice notice = noticeRequestDTO.toEntity(member);
        Notice savedNotice = noticeRepository.save(notice);
        log.info("Notice created successfully. Notice ID: {}", savedNotice.getNoticeId());
        return new NoticeResponseDTO(savedNotice);
    }

    //공지사항 수정
    public NoticeResponseDTO updateNotice(NoticeUpdateDTO noticeUpdateDTO, String username, Long noticeId){
        Member member = getMember(username);
        //관리자 아닐경우 예외 발생
        if (!member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw new AccessDeniedException("공지사항 접근 권한이 없습니다.");
        }
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(ErrorCode.NOTICE_NOT_FOUND::throwException);

        try{
            notice.changeTitle(noticeUpdateDTO.getTitle());
            notice.changeContent(noticeUpdateDTO.getContent());
            notice.changeMember(member); //수정한 관리자에 대한 정보 반영
            return new NoticeResponseDTO(notice);
        }catch (Exception e){
            log.error("updateNotice error: {}",  e.getMessage());
            throw ErrorCode.NOTICE_UPDATE_FAIL.throwException();
        }
    }



    //공지사항 삭제
    public boolean deleteNotice(Long noticeId, String username){
        Member member = getMember(username);
        //관리자 아닐경우 예외 발생
        if (!member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw new AccessDeniedException("공지사항 접근 권한이 없습니다.");
        }
        noticeRepository.findById(noticeId).orElseThrow(ErrorCode.NOTICE_NOT_FOUND::throwException);
        try {
            noticeRepository.deleteById(noticeId);
            return true;
        }catch (Exception e) {
            log.error("deleteNotice error: {}",  e.getMessage());
            throw ErrorCode.NOTICE_DELETE_FAIL.throwException();
        }
    }

    private Member getMember(String username) {
        Member member = memberRepository.findByEmail(username).get();
        if (member == null) {
            throw ErrorCode.MEMBER_NOT_FOUND.throwException();
        }
        return member;
    }
}
