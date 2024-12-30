package com.hunmin.domain.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.word.entity.Word;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.domain.word.repository.WordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class WordRepositoryTest {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 단어 등록 테스트
    @Test
    @Commit
    public void testCreateWord() {
        Member member = memberRepository.findById(105L).get();  // ADMIN 계정

        Word word = Word.builder()
                .title("단어테스트")
                .lang("영어")
                .translation("번역테스트")
                .definition("정의테스트")
                .build();

        Word savedWord = wordRepository.save(word);

        assertNotNull(savedWord);
    }

    // 단어 조회 테스트
    @Test
    public void testWordRead() {
        Long wordId = 1L;
        Word word = wordRepository.findById(wordId).orElseThrow();

        assertNotNull(word);
        System.out.println("단어 조회 : " + word.getTitle() + ", " + word.getLang() + ", " + word.getTranslation() + ", " + word.getDefinition());
    }

    //게시글 수정 테스트
    @Test
    @Transactional
    @Commit
    public void testUpdateWord() {
        Long wordId = 24665L;

        String updateTitle = "단어 수정 테스트";
        String updatedLang = "일본어"; // 수정할 언어
        String updatedTranslation = "단어 수정 테스트 번역";
        String updatedDefinition = "단어 수정 테스트 정의";

        Word word = wordRepository.findById(wordId).orElseThrow();

        word.changeWord(updateTitle);
        word.changeLang(updatedLang);
        word.changeTranslation(updatedTranslation);
        word.changeDefinition(updatedDefinition);

        wordRepository.save(word);

        word = wordRepository.findById(wordId).orElseThrow();

        assertEquals(updateTitle, word.getTitle());
        assertEquals(updatedLang, word.getLang());
        assertEquals(updatedTranslation, word.getTranslation());
        assertEquals(updatedDefinition, word.getDefinition());

        System.out.println("수정 테스트 : " + updateTitle + ", " + updatedLang + ", " + updatedTranslation + ", " + updatedDefinition);
    }

    @Test
    @Transactional
    @Commit
    public void testDeleteWord() {
        Long wordId = 24665L;

        wordRepository.deleteById(wordId);

        assertTrue(wordRepository.findById(wordId).isEmpty());
    }

    @Test
    public void testReadWordList(){
        Pageable pageable = PageRequest.of(0, 20);

        Page<Word> words = wordRepository.findAll(pageable);

        assertNotNull(words);
    }

    @Test
    public void testReadWordListByLang() {
        String lang = "영어";
        Pageable pageable = PageRequest.of(0, 20);

        Page<Word> words = wordRepository.findByLang(lang, pageable);

        assertNotNull(words);
    }
}
