package com.hunmin.domain.board.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hunmin.domain.board.dto.BoardRequestDTO;
import com.hunmin.domain.board.dto.BoardResponseDTO;
import com.hunmin.global.common.PageRequestDTO;
import com.hunmin.domain.board.entity.Board;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.board.repository.BoardRepository;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.HashOperations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BoardService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, BoardResponseDTO> hashOps;

    @Autowired
    public BoardService(MemberRepository memberRepository, BoardRepository boardRepository,
                        RedisTemplate<String, Object> redisTemplate) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.redisTemplate = redisTemplate;
        this.hashOps = redisTemplate.opsForHash();
    }

    //Redis에 저장된 BoardResponseDTO 읽기
    public BoardResponseDTO readBoardFromRedis(String boardId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> boardData = (Map<String, Object>) hashOps.get("board", boardId);

            return objectMapper.convertValue(boardData, BoardResponseDTO.class);
        } catch (Exception e) {
            log.error("Error reading board from Redis: ", e);
            return null;
        }
    }

    //게시글 이미지 첨부
    public String uploadImage(MultipartFile file) throws IOException {
        String uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toString();
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory");
            }
        }

        String fileName = UUID.randomUUID() + "." + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + fileName;
    }

    //파일 확장자 추출
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }

        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    //게시글 이미지 삭제
    public void deleteImage(String imageUrl) throws IOException {
        String uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toString();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(uploadDir, fileName);

        Files.deleteIfExists(filePath);
    }

    //게시글 등록
    public BoardResponseDTO createBoard(BoardRequestDTO boardRequestDTO) {
        try {
            Member member = memberRepository.findById(boardRequestDTO.getMemberId())
                    .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);

            Board board = Board.builder()
                    .member(member)
                    .title(boardRequestDTO.getTitle())
                    .content(boardRequestDTO.getContent())
                    .location(boardRequestDTO.getLocation())
                    .latitude(boardRequestDTO.getLatitude())
                    .longitude(boardRequestDTO.getLongitude())
                    .imageUrls(boardRequestDTO.getImageUrls() != null ? boardRequestDTO.getImageUrls() : new ArrayList<>())
                    .build();

            boardRepository.save(board);

            hashOps.put("board", String.valueOf(board.getBoardId()), new BoardResponseDTO(board));

            return new BoardResponseDTO(board);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.BOARD_CREATE_FAIL.throwException();
        }
    }

    //게시글 조회
    public BoardResponseDTO readBoard(Long boardId) {
        BoardResponseDTO cachedBoard = readBoardFromRedis(String.valueOf(boardId));
        if (cachedBoard != null) {
            return cachedBoard;
        }

        Board board = boardRepository.findCommentsByBoardId(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);
        BoardResponseDTO boardResponseDTO = new BoardResponseDTO(board);

        hashOps.put("board", String.valueOf(boardId), boardResponseDTO);

        return boardResponseDTO;
    }

    //게시글 수정
    public BoardResponseDTO updateBoard(Long boardId, BoardRequestDTO boardRequestDTO) {
        Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);

        try {
            List<String> existingImageUrls = new ArrayList<>(board.getImageUrls());

            if (boardRequestDTO.getImageUrls() != null) {
                List<String> newImageUrls = boardRequestDTO.getImageUrls();
                List<String> urlsToDelete = new ArrayList<>();

                for (String existingUrl : existingImageUrls) {
                    if (!newImageUrls.contains(existingUrl)) {
                        urlsToDelete.add(existingUrl);
                    }
                }

                for (String url : urlsToDelete) {
                    deleteImage(url);
                    existingImageUrls.remove(url);
                }

                existingImageUrls.addAll(newImageUrls);
                board.changeImgUrls(existingImageUrls);
            }

            board.changeTitle(boardRequestDTO.getTitle());
            board.changeContent(boardRequestDTO.getContent());
            board.changeLocation(boardRequestDTO.getLocation());
            board.changeLatitude(boardRequestDTO.getLatitude());
            board.changeLongitude(boardRequestDTO.getLongitude());

            boardRepository.save(board);

            hashOps.put("board", String.valueOf(board.getBoardId()), new BoardResponseDTO(board));

            return new BoardResponseDTO(board);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.BOARD_UPDATE_FAIL.throwException();
        }
    }

    //게시글 삭제
    public BoardResponseDTO deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(ErrorCode.BOARD_NOT_FOUND::throwException);

        try {
            for (String imageUrl : board.getImageUrls()) {
                deleteImage(imageUrl);
            }

            boardRepository.delete(board);
            hashOps.delete("board", String.valueOf(boardId));

            return new BoardResponseDTO(board);
        } catch (Exception e) {
            log.error(e);
            throw ErrorCode.BOARD_DELETE_FAIL.throwException();
        }
    }

    //게시글 목록 조회
    public Page<BoardResponseDTO> readBoardList(PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage()-1, 10, sort);

        List<BoardResponseDTO> boardResponseDTOs = new ArrayList<>();

        for (Object boardIdObj : redisTemplate.opsForHash().keys("board")) {
            if (boardIdObj instanceof String boardId) {
                BoardResponseDTO cachedBoard = readBoardFromRedis(boardId);
                if (cachedBoard != null) {
                    boardResponseDTOs.add(cachedBoard);
                }
            }
        }

        if (boardResponseDTOs.size() < pageable.getPageSize()) {
            Page<Board> boards = boardRepository.findAll(pageable);
            List<BoardResponseDTO> newBoardResponseDTOs = boards.map(BoardResponseDTO::new).getContent();

            for (BoardResponseDTO boardResponseDTO : newBoardResponseDTOs) {
                if (boardResponseDTOs.stream().noneMatch(b -> b.getBoardId().equals(boardResponseDTO.getBoardId()))) {
                    hashOps.put("board", String.valueOf(boardResponseDTO.getBoardId()), boardResponseDTO);
                    boardResponseDTOs.add(boardResponseDTO);
                }
            }
        }

        boardResponseDTOs.sort((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), boardResponseDTOs.size());
        List<BoardResponseDTO> pagedResponse = boardResponseDTOs.subList(start, end);

        return new PageImpl<>(pagedResponse, pageable, boardResponseDTOs.size());
    }

    //회원 별 작성글 목록 조회
    public Page<BoardResponseDTO> readBoardListByMember(Long memberId, PageRequestDTO pageRequestDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = pageRequestDTO.getPageable(sort);

        List<BoardResponseDTO> boardResponseDTOs = new ArrayList<>();

        for (Object boardIdObj : redisTemplate.opsForHash().keys("board")) {
            if (boardIdObj instanceof String boardId) {
                BoardResponseDTO cachedBoard = readBoardFromRedis(boardId);
                if (cachedBoard != null && cachedBoard.getMemberId().equals(memberId)) {
                    boardResponseDTOs.add(cachedBoard);
                }
            }
        }

        if (boardResponseDTOs.size() < pageable.getPageSize()) {
            Page<Board> boards = boardRepository.findByMemberId(memberId, pageable);
            List<BoardResponseDTO> newBoardResponseDTOs = boards.map(BoardResponseDTO::new).getContent();

            for (BoardResponseDTO boardResponseDTO : newBoardResponseDTOs) {
                if (boardResponseDTOs.stream().noneMatch(b -> b.getBoardId().equals(boardResponseDTO.getBoardId()))) {
                    hashOps.put("board", String.valueOf(boardResponseDTO.getBoardId()), boardResponseDTO);
                    boardResponseDTOs.add(boardResponseDTO);
                }
            }
        }

        boardResponseDTOs.sort((b1, b2) -> b2.getCreatedAt().compareTo(b1.getCreatedAt()));

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), boardResponseDTOs.size());
        List<BoardResponseDTO> pagedResponse = boardResponseDTOs.subList(start, end);

        return new PageImpl<>(pagedResponse, pageable, boardResponseDTOs.size());
    }

    public Page<Board> searchBoardByTitle(String title, Pageable pageable) {
        if(title == null) title = "";
        log.info("title321: {}", title);
        try {
            Sort sort = Sort.by(Sort.Direction.DESC, "title");
            log.info("pageable: {}", pageable.toString());
        return boardRepository.findByTitleContaining(title, pageable);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw ErrorCode.BOARD_NOT_FOUND.throwException();
        }
    }
}
