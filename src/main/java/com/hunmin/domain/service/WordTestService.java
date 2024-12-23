package com.hunmin.domain.service;

import com.hunmin.domain.dto.word.WordResponseDTO;
import com.hunmin.domain.dto.word.WordScoreRequestDTO;
import com.hunmin.domain.dto.word.WordScoreResponseDTO;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.Word;
import com.hunmin.domain.entity.WordScore;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.domain.repository.WordRepository;
import com.hunmin.domain.repository.WordScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WordTestService {
    private final WordRepository wordRepository;
    private final WordScoreRepository wordScoreRepository;
    private final MemberRepository memberRepository;

    // 선택한 언어와 레벨에 따라 랜덤한 단어를 가져오는 메서드
    public List<WordResponseDTO> getRandomTestWords(String lang, String level) {
        // 언어에 따라 단어 리스트 가져오기
        List<Word> words = wordRepository.findByLang(lang);
        // 단어 랜덤 섞기
        Collections.shuffle(words);
        // 지정된 레벨에 따라 문제 수 선택
        List<Word> selectedWords = words.stream()
                .limit(getQuestionCount(level))
                .collect(Collectors.toList());

        // 선택된 단어들을 DTO로 변환하여 반환
        return selectedWords.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // 레벨에 따른 문제 수 반환
    private long getQuestionCount(String level) {
        switch (level) {
            case "1": return 25; // 난이도 하
            case "2": return 50; // 난이도 중
            case "3": return 100; // 난이도 상
            default: throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    // 단어를 DTO로 변환하는 메서드
    private WordResponseDTO mapToResponseDTO(Word word) {
        WordResponseDTO dto = new WordResponseDTO(word);

        dto.setDisplayWord(dto.getTranslation());
        dto.setDisplayTranslation(dto.getTitle());
        dto.setDefinition(word.getDefinition());

        return dto;
    }

    @Transactional
    public Map<String, Object> submitAnswers(WordScoreRequestDTO requestDTO) {
        int finalScore = calculateFinalScore(requestDTO.getCorrectCount(), requestDTO.getTestLevel());
        double penaltyScore = calculatePenaltyScore(finalScore, requestDTO.getTestLevel());

        requestDTO.setScore(finalScore, penaltyScore);
        saveTestScore(requestDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("finalScore", finalScore);
        result.put("penaltyScore", penaltyScore);
        return result;
    }

    @Transactional
    public void saveTestScore(WordScoreRequestDTO requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        WordScore wordScore = requestDTO.toEntity(member);

        wordScoreRepository.save(wordScore);
    }

    // 정답 개수 * 레벨에 해당되는 문제 하나당 점수
    private int calculateFinalScore(int correctCount, String testLevel) {
        int pointsPerCorrect = getPoint(testLevel);
        return correctCount * pointsPerCorrect;
    }

    // 최종 점수에 레벨에 해당되는 페널티 부여
    public double calculatePenaltyScore(int finalScore, String testLevel) {
        double penaltyPercent = getPenalty(testLevel);
        return finalScore * (1 - penaltyPercent);
    }

    // 레벨에 따른 점수 반환
    public int getPoint(String testLevel) {
        return switch (testLevel) {
            case "1" -> 4; // 난이도 하
            case "2" -> 2; // 난이도 중
            case "3" -> 1; // 난이도 상
            default -> 0; // 잘못된 레벨
        };
    }

    // 레벨에 따른 패널티 점수
    public double getPenalty (String testLevel) {
        return switch (testLevel) {
            case "1" -> 0.20;
            case "2" -> 0.10;
            case "3" -> 0.0;
            default -> throw new IllegalArgumentException("Invalid level: " + testLevel);
        };
    }

    // 전체 랭킹 순위
    public List<WordScoreResponseDTO> getRankings() {
        List<WordScore> wordScores = wordScoreRepository.getTopRankers();

        // 랭킹 기록이 없을 경우 빈 리스트 반환
        if (wordScores.isEmpty()) {
            return Collections.emptyList();
        }

        // WordScore를 WordScoreResponseDTO로 변환
        return wordScores.stream()
                .map(WordScoreResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 개인 시험 기록
    public List<WordScoreResponseDTO> getUserTestScores(Long memberId) {
        List<WordScore> wordScores = wordScoreRepository.getUserScores(memberId);

        // 시험 기록이 없을 경우 빈 리스트 반환
        if (wordScores.isEmpty()) {
            return Collections.emptyList();
        }

        // WordScore를 WordScoreResponseDTO로 변환
        return wordScores.stream()
                .map(WordScoreResponseDTO::new)
                .collect(Collectors.toList());
    }
}
