package com.hunmin.domain.word.repository;

import com.hunmin.domain.word.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends JpaRepository<Word, Long> {
    Optional<Word> findByTitle(String title);
    Optional<Word> findById(Long id);
    Page<Word> findByLang(String lang, Pageable pageable);

    Optional<Word> findByTitleAndLang(String title, String lang);
    long countByTitleAndLang(String title, String lang);

    List<Word> findByLang(String lang);
}
