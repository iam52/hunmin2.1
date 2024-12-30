package com.hunmin.domain.wordtest.controller;

import com.hunmin.domain.word.dto.WordResponseDTO;
import com.hunmin.domain.word.dto.WordScoreRequestDTO;
import com.hunmin.domain.word.dto.WordScoreResponseDTO;
import com.hunmin.domain.wordtest.service.WordTestService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/words/test")
public class WordTestController {
    private final WordTestService wordTestService;

    @Autowired
    public WordTestController(WordTestService wordTestService) {
        this.wordTestService = wordTestService;
    }

    @GetMapping ("/testLanguageSelect")
    public String getTestLanguagePage(){
        return "word/testLanguage";
    }

    @GetMapping ("/testLevelSelect")
    public String getTestLevelPage(@RequestParam String lang, Model model){
        model.addAttribute("lang", lang);
        return "word/testLevelSelect";
    }

    @GetMapping ("/start")
    public ResponseEntity<Map<String, Object>> getRandomTestWords (@RequestParam String lang, @RequestParam String level) {
        List<WordResponseDTO> randomTestWords = wordTestService.getRandomTestWords(lang, level);

        long displayTime = randomTestWords.isEmpty() ? 0 : randomTestWords.get(0).getDisplayTime();

        Map<String, Object>  response = new HashMap<>();
        response.put("words", randomTestWords);
        response.put("level", level);
        response.put("lang", lang);
        response.put("displayTime", displayTime);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitAnswers(@RequestBody WordScoreRequestDTO wordScoreRequestDTO){
        Map<String, Object> scoreResult = wordTestService.submitAnswers(wordScoreRequestDTO);

        return ResponseEntity.ok(scoreResult);
    }

    @GetMapping("/rankings")
    public List<WordScoreResponseDTO> getRankings() {
        return wordTestService.getRankings();
    }

    @GetMapping("/records")
    public List<WordScoreResponseDTO> getUserTestScores(@RequestParam Long memberId) {
        return wordTestService.getUserTestScores(memberId);
    }
}
