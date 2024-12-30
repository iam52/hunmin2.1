package com.hunmin.domain.wordtest.repository;

import com.hunmin.domain.wordtest.entity.WordScore;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WordScoreRepository extends JpaRepository<WordScore, Long> {
    // 전체 점수 랭킹
    @Query("SELECT ws FROM WordScore ws ORDER BY ws.testScore DESC, ws.testLevel DESC, ws.createdAt ASC")
    List<WordScore> getTopRankers();

    // 개인 시험 기록 조회
    @Query("SELECT ws FROM WordScore ws WHERE ws.member.memberId = :memberId ORDER BY ws.createdAt DESC")
    List<WordScore> getUserScores(@Param("memberId") Long memberId);
}
