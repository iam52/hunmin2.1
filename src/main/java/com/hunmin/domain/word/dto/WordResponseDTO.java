package com.hunmin.domain.word.dto;

import com.hunmin.domain.word.entity.Word;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WordResponseDTO {
    private Long wordId;                // PK
    private String title;               // 단어(한국어)
    private String lang;                // 언어
    private LocalDateTime createdAt;    // 작성날짜
    private LocalDateTime updatedAt;    // 수정날짜
    private String definition;          // 정의
    private String translation;         // 번역(영어)

    private String displayWord;
    private long displayTime;
    private String displayTranslation;



    public WordResponseDTO(Word word) {
        this.wordId = word.getWordId();
        this.title = word.getTitle();
        this.lang = word.getLang();
        this.createdAt = word.getCreatedAt();
        this.updatedAt = word.getUpdatedAt();
        this.definition = word.getDefinition();
        this.translation = word.getTranslation();
    }

    // DTO에서 엔티티로 변환하는 메서드 추가
    public Word toEntity() {
        return Word.builder()
                .title(this.title)
                .lang(this.lang)
                .definition(this.definition)
                .translation(this.translation)
                .build();
    }

    public void setDisplayWord(String displayWord) {
        this.displayWord = displayWord;
    }

    public void setDisplayTranslation(String displayTranslation) {
        this.displayTranslation = displayTranslation;
    }

    public void setDisplayTime(long displayTime) {
        this.displayTime = displayTime;
    }
}
