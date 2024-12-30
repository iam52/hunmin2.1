package com.hunmin.domain.word.service;

import com.hunmin.domain.wordlearning.dto.WordPageRequestDTO;
import com.hunmin.domain.word.dto.WordRequestDTO;
import com.hunmin.domain.word.dto.WordResponseDTO;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.domain.word.entity.Word;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.domain.word.repository.WordRepository;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class WordService {
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;
    private Member getMember(String username) {
        Member member = memberRepository.findByEmail(username).get();
        if (member == null) {
            throw ErrorCode.MEMBER_NOT_FOUND.throwException();
        }
        return member;
    }

    // 단어 등록
    public WordResponseDTO createWord(WordRequestDTO wordRequestDTO, String username) {
        Member member = getMember(username);
        if (!member.getMemberRole().equals(MemberRole.ADMIN)){
            throw ErrorCode.MEMBER_INVALID_INPUT.throwException();
        }
        try {
            Word word = wordRequestDTO.toEntity(member);
            Word saveWord = wordRepository.save(word);
            return new WordResponseDTO(saveWord);
        } catch (Exception e) {
            log.error("createNotice error: {}",  e.getMessage());
            throw ErrorCode.WORD_CREATE_FAIL.throwException();
        }
    }

    // 단어 수정
    public WordResponseDTO updateWord(WordRequestDTO wordRequestDTO, String title, String lang, String username) {
        Member member = getMember(username);
        if (!member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw ErrorCode.MEMBER_INVALID_INPUT.throwException();
        }
        // title과 lang 조합으로 단어 조회
        Word word = wordRepository.findByTitleAndLang(title, lang).orElseThrow(ErrorCode.WORD_NOT_FOUND::throwException);

        try {
            // 속성 수정
            word.changeWord(wordRequestDTO.getTitle()); // 새로운 제목으로 변경
            word.changeLang(wordRequestDTO.getLang());   // 새로운 언어로 변경
            word.changeTranslation(wordRequestDTO.getTranslation()); // 새로운 번역으로 변경
            word.changeDefinition(wordRequestDTO.getDefinition()); // 새로운 정의로 변경
            wordRepository.save(word); // 업데이트 반영
            return new WordResponseDTO(word);
        } catch (Exception e) {
            log.error("updateWord error: {}", e.getMessage());
            throw ErrorCode.WORD_UPDATE_FAIL.throwException();
        }
    }

    // 단어 삭제
    public boolean deleteWordByTitleAndLang(String title, String lang, String username) {
        Member member = getMember(username);
        // 관리자가 아닐경우 예외 발생
        if (!member.getMemberRole().equals(MemberRole.ADMIN)) {
            throw ErrorCode.MEMBER_INVALID_INPUT.throwException();
        }
        // title과 lang 조합으로 단어 조회
        Word word = wordRepository.findByTitleAndLang(title, lang).orElseThrow(ErrorCode.WORD_NOT_FOUND::throwException);
        try {
            // 단어 삭제
            wordRepository.delete(word);
            return true;
        } catch (Exception e) {
            log.error("deleteWord error: {}", e.getMessage());
            throw ErrorCode.WORD_DELETE_FAIL.throwException();
        }
    }

    // 단어 조회
    public WordResponseDTO getWordByTitleAndLang(String title, String lang) {
        // title과 lang 조합으로 단어 찾기
        Word word = wordRepository.findByTitleAndLang(title, lang).orElseThrow(ErrorCode.WORD_NOT_FOUND::throwException);
        return new WordResponseDTO(word);
    }

    // 전체 단어 조회
    public Page<WordResponseDTO> getAllWords(WordPageRequestDTO wordPageRequestDTO, String lang) {
        Pageable pageable = wordPageRequestDTO.getPageable(Sort.by("title")); // 기본 정렬 기준 설정
        Page<Word> words;
        // 언어가 null이거나 비어있을 경우 모든 언어의 단어 반환
        if (lang == null || lang.isEmpty()) {
            words = wordRepository.findAll(pageable);
        } else {
            words = wordRepository.findByLang(lang, pageable); // 선택된 언어의 단어 반환
        }
        // Page<Word>를 Page<WordResponseDTO>로 변환하여 반환
        return words.map(WordResponseDTO::new);
    }
}
