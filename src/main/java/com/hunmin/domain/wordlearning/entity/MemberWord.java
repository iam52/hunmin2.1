package com.hunmin.domain.wordlearning.entity;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.word.entity.Word;
import com.hunmin.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberWord extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberWordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="word_id")
    private Word word;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


}
