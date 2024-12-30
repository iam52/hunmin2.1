package com.hunmin.domain.admin.service;

import com.hunmin.domain.board.dto.BoardResponseDTO;
import com.hunmin.domain.comment.dto.CommentResponseDTO;
import com.hunmin.domain.member.dto.MemberStatusDTO;
import com.hunmin.global.common.PageRequestDTO;
import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.comment.entity.Comment;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.board.repository.BoardRepository;
import com.hunmin.domain.comment.repository.CommentRepository;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AdminService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    //회원 검색
    public MemberStatusDTO getMemberByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        int boardCount = boardRepository.countByMemberId(member.getMemberId());
        int commentCount = commentRepository.countByMemberId(member.getMemberId());
        return new MemberStatusDTO(member, boardCount, commentCount);
    }

    // 회원 닉네임으로 검색
    public MemberStatusDTO getMemberByNickname(String username) {
        Member member = memberRepository.findByNickname(username).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        int boardCount = boardRepository.countByMemberId(member.getMemberId());
        int commentCount = commentRepository.countByMemberId(member.getMemberId());
        return new MemberStatusDTO(member, boardCount, commentCount);
    }

    // 회원 목록 조회
    public Page<MemberStatusDTO> getAllMembers(PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, 10, sort);
        try {
            return memberRepository.findAll(pageable).map(member -> {
                int boardCount = boardRepository.countByMemberId(member.getMemberId());
                int commentCount = commentRepository.countByMemberId(member.getMemberId());
                return new MemberStatusDTO(member, boardCount, commentCount);
            });
        } catch (Exception e) {
            log.error("getAllMembers : {}", e.getMessage());
            throw ErrorCode.MEMBER_NOT_FOUND.throwException();
        }
    }

    //회원별 작성글 목록
    public Page<BoardResponseDTO> getBoardsByMemberId(Long memberId, PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, 10, sort);

        Page<Board> boardPage = boardRepository.findByMemberId(memberId, pageable);

        try {
            // List<BoardResponseDTO>로 변환
            List<BoardResponseDTO> boardResponseDTOs = new ArrayList<>();
            for (Board board : boardPage.getContent()) {
                BoardResponseDTO dto = new BoardResponseDTO(board);
                boardResponseDTOs.add(dto);
            }
            // Page<BoardResponseDTO> 생성
            return new PageImpl<>(boardResponseDTOs, pageable, boardPage.getTotalElements());
        } catch (Exception e) {
            log.error("getBoardsByMemberId : {}", e.getMessage());
            throw ErrorCode.BOARD_NOT_FOUND.throwException();
        }
    }

    //회원별 댓글 목록
    public Page<CommentResponseDTO> getCommentsByMemberId(Long memberId, PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, 10, sort);

        // Page<Comment>로 반환받기
        Page<Comment> commentPage = commentRepository.findByMemberId(memberId, pageable);

        try {
            // List<CommentResponseDTO>로 변환
            List<CommentResponseDTO> commentResponseDTOs = new ArrayList<>();
            for (Comment comment : commentPage.getContent()) {
                CommentResponseDTO dto = new CommentResponseDTO(comment);
                commentResponseDTOs.add(dto);
            }
            // Page<CommentResponseDTO> 생성
            return new PageImpl<>(commentResponseDTOs, pageable, commentPage.getTotalElements());
        } catch (Exception e) {
            log.error("getCommentsByMemberId : {}", e.getMessage());
            throw ErrorCode.COMMENT_NOT_FOUND.throwException();
        }
    }
}


