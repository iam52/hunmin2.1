package com.hunmin.domain.word.controller;

import com.hunmin.domain.wordlearning.dto.WordPageRequestDTO;
import com.hunmin.domain.word.dto.WordRequestDTO;
import com.hunmin.domain.word.dto.WordResponseDTO;
import com.hunmin.domain.word.repository.WordRepository;
import com.hunmin.domain.word.service.WordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/words")
@Tag(name = "단어", description = "단어 CRUD")
public class WordController {
    private final WordService wordService;
    private final WordRepository wordRepository;

    // 단어 등록
    @PostMapping
    @Operation(summary = "단어 등록", description = "단어를 등록할때 사용하는 API")
    public ResponseEntity<WordResponseDTO> createWord(@Validated @RequestBody WordRequestDTO wordRequestDTO, @AuthenticationPrincipal UserDetails username) {
        WordResponseDTO word = wordService.createWord(wordRequestDTO, username.getUsername()); //이메일 반환
        return ResponseEntity.ok(word);
    }

    // 단어 조회
    @GetMapping("/join/{title}/{lang}")
    @Operation(summary = "단어 조회", description = "특정 단어를 조회할때 사용하는 API")
    public ResponseEntity<WordResponseDTO> read(@PathVariable("title") String title,
                                                @PathVariable("lang") String lang) {
        // 서비스 호출하여 단어 조회
        WordResponseDTO wordResponseDTO = wordService.getWordByTitleAndLang(title, lang);
        return ResponseEntity.ok(wordResponseDTO);
    }

    // 단어 전체 조회
    @GetMapping
    @Operation(summary = "단어 전체 조회", description = "모든 단어를 조회할때 사용하는 API")
    public ResponseEntity<Page<WordResponseDTO>> getWordlist(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "25") int size,
            @RequestParam(value = "lang", required = false) String lang) {

        // WordPageRequestDTO 객체 생성
        WordPageRequestDTO wordPageRequestDTO = WordPageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();

        // Page<WordResponseDTO> 형태로 반환
        // 수정된 부분: 기존에 Page<Word>를 가져오고 다시 map을 사용하던 코드를 제거
        Page<WordResponseDTO> response = wordService.getAllWords(wordPageRequestDTO, lang);

        return ResponseEntity.ok(response); // 응답으로 반환
    }

    // 단어 수정
    @PutMapping("/update/{title}/{lang}")
    @Operation(summary = "단어 수정", description = "단어를 수정할때 사용하는 API")
    public ResponseEntity<WordResponseDTO> updateWord(@Validated @RequestBody WordRequestDTO wordRequestDTO,
                                                      @PathVariable("title") String title,
                                                      @PathVariable("lang") String lang,
                                                      @AuthenticationPrincipal UserDetails username) {
        // 서비스 호출하여 단어 수정
        WordResponseDTO updatedWord = wordService.updateWord(wordRequestDTO, title, lang, username.getUsername());

        // 업데이트된 DTO 반환
        return ResponseEntity.ok(updatedWord);
    }

    // 단어 삭제
    @DeleteMapping("/delete/{title}/{lang}")
    @Operation(summary = "단어 삭제", description = "단어를 삭제할때 사용하는 API")
    public ResponseEntity<Map<String, String>> deleteWord(@PathVariable("title") String title,
                                                          @PathVariable("lang") String lang,
                                                          @AuthenticationPrincipal UserDetails username) {

        wordService.deleteWordByTitleAndLang(title, lang, username.getUsername());

        return ResponseEntity.ok(Map.of("result", "success"));
    }
}
