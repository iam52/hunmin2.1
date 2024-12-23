package com.hunmin.domain.service;

import com.hunmin.domain.dto.notice.*;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.entity.Notice;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.domain.repository.NoticeRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            throw ErrorCode.MEMBER_INVALID.throwException();
        }
            try {
                Notice notice = noticeRequestDTO.toEntity(member);
                Notice savedNotice = noticeRepository.save(notice);
                log.info("Notice created successfully. Notice ID: {}", savedNotice.getNoticeId());
                return new NoticeResponseDTO(savedNotice);
            }catch (Exception e) {
                log.error("createNotice error: {}",  e.getMessage());
                throw ErrorCode.NOTICE_CREATE_FAIL.throwException();
            }
    }

    //공지사항 수정
    public NoticeResponseDTO updateNotice(NoticeUpdateDTO noticeUpdateDTO, String username, Long noticeId){
        Member member = getMember(username);
        //관리자 아닐경우 예외 발생
        if (!member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw ErrorCode.MEMBER_INVALID.throwException();
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
            throw ErrorCode.MEMBER_INVALID.throwException();
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
        Optional<Member> member = memberRepository.findByEmail(username);
        if (member.isEmpty()) {
            throw ErrorCode.MEMBER_NOT_FOUND.throwException();
        }
        return member.orElse(null);
    }
}
