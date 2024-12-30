package com.hunmin.domain.wordlearning.service;

import com.hunmin.domain.word.dto.WordResponseDTO;
import com.hunmin.domain.word.entity.Word;
import com.hunmin.domain.word.repository.WordRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordLearningService {
    private final WordRepository wordRepository;

    @Autowired
    public WordLearningService(WordRepository wordRepository) {
        this.wordRepository = wordRepository;
    }

    public List<WordResponseDTO> getRandomWords(String lang, int level) {
        List<Word> words = wordRepository.findByLang(lang);
        System.out.println("Words fetched: " + words.size()); // 추가된 로그

        // 랜덤으로 30개 단어 선택
        Collections.shuffle(words);
        List<Word> selectedWords = words.stream().limit(30).collect(Collectors.toList());

        // 랜덤하게 보여줄 단어 설정
        List<WordResponseDTO> responseList = selectedWords.stream()
                .map(word -> {
                    WordResponseDTO dto = new WordResponseDTO(word);

                    // title과 translation 중 랜덤으로 선택
                    if (Math.random() < 0.5) {
                        dto.setDisplayWord(dto.getTitle()); // 제목을 표시
                        dto.setDisplayTranslation(dto.getTranslation()); // 번역을 표시
                    } else {
                        dto.setDisplayWord(dto.getTranslation()); // 번역을 표시
                        dto.setDisplayTranslation(dto.getTitle()); // 제목을 표시
                    }

                    dto.setDisplayTime(getDisplayTime(level)); // 레벨에 따른 표시 시간 설정

                    // 각 단어의 정의를 DTO에 설정
                    dto.setDefinition(word.getDefinition()); // definition 추가

                    return dto; // 완성된 DTO 반환
                })
                .collect(Collectors.toList());

        return responseList; // 최종 리스트 반환
    }

    private long getDisplayTime(int level) {
        switch (level) {
            case 1: return 15000; // 10초
            case 2: return 11000;  // 7초
            case 3: return 3000;  // 3초
            default: throw new IllegalArgumentException("Invalid level: " + level);
        }
    }
}
