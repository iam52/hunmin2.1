package com.hunmin.domain.wordlearning.controller;

import com.hunmin.domain.word.dto.WordResponseDTO;
import com.hunmin.domain.wordlearning.service.WordLearningService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/words/learning")
public class WordLearningController {
    private final WordLearningService wordLearningService;

    @Autowired
    public WordLearningController(WordLearningService wordLearningService) {
        this.wordLearningService = wordLearningService;
    }

    @GetMapping("/languageSelect")
    public String getLearningLanguagePage() {
        return "word/learningLanguage"; // word/learningLanguage.html 파일을 렌더링합니다.
    }

    @GetMapping("/levelSelect")
    public String levelPage(@RequestParam String lang, Model model) {
        model.addAttribute("lang", lang); // 선택한 언어를 모델에 추가
        return "word/levelSelect"; // 레벨 선택 페이지로 이동
    }

    @GetMapping("/start")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRandomWords(@RequestParam String lang, @RequestParam int level) {
        List<WordResponseDTO> randomWords = wordLearningService.getRandomWords(lang, level);

        long displayTime = randomWords.isEmpty() ? 0 : randomWords.get(0).getDisplayTime();

        Map<String, Object> response = new HashMap<>();
        response.put("words", randomWords);
        response.put("level", level);
        response.put("lang", lang);
        response.put("displayTime", displayTime);

        return ResponseEntity.ok(response);
    }
}
