package com.hunmin.domain.word.entity;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.global.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Word extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wordId;            // PK
    private String title;           // 단어(한국어)
    private String lang;            // 언어

    @Column(length = 1000)
    private String translation;     // 번역(영어)

    @Column(length = 1000)
    private String definition;      // 정의

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    public void changeWord(String title){
        this.title = title;
    }

    public void changeTranslation(String translation){
        this.translation = translation;
    }

    public void changeLang(String lang){
        this.lang = lang;
    }

    public void changeDefinition(String definition){
        this.definition = definition;
    }
}