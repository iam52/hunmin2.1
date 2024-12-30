package com.hunmin.domain.word.dto;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.word.entity.Word;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class WordRequestDTO {

    private Long memberId;

    private Long wordId;

    @NotEmpty(message = "명칭은 필수 입력값입니다.")
    private String title;

    @NotEmpty(message = "번역은 필수 입력값입니다.")
    private String translation;

    @NotEmpty(message = "정의는 필수 입력값입니다.")
    private String definition;

    @NotEmpty(message = "언어는 필수 입력값입니다.")
    private String lang;

    private Long testScore;

    public Word toEntity() {
        return Word.builder()
                .wordId(wordId)
                .title(title)
                .translation(translation)
                .definition(definition)
                .lang(lang)
                .build();
    }

    public Word toEntity(Member member){
        return Word.builder()
                .member(member)
                .title(title)
                .translation(translation)
                .definition(definition)
                .lang(lang)
                .build();
    }
}