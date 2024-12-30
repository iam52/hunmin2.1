package com.hunmin.domain.notice.repository;

import com.hunmin.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.member ")
    Page<Notice> findAllNoticesResponse(Pageable pageable);
}
