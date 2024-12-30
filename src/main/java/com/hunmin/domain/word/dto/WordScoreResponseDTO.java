package com.hunmin.domain.word.dto;

import com.hunmin.domain.wordtest.entity.WordScore;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Data
public class WordScoreResponseDTO {
    private Long wordScoreId;
    private Long memberId;
    private int testRank;
    private String nickName;
    private String profileImage;
    private String testLang;
    private String testLevel;
    private int testScore;
    private Double testRankScore;
    private LocalDateTime createdAt;

    public WordScoreResponseDTO(WordScore wordScore) {
        this.wordScoreId = wordScore.getWordScoreId();
        this.memberId = wordScore.getMember().getMemberId();
        this.nickName = wordScore.getMember().getNickname();
        this.profileImage = wordScore.getMember().getImage();
        this.testLang = wordScore.getTestLang();
        this.testLevel = wordScore.getTestLevel();
        this.testScore = wordScore.getTestScore();
        this.testRankScore = wordScore.getTestRankScore();
        this.createdAt = wordScore.getCreatedAt();
    }
}
