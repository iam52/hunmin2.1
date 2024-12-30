package com.hunmin.domain.follow.repository.search;

import com.hunmin.domain.follow.dto.FollowRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowSearch {
    Page<FollowRequestDTO> getFollowPage(Long followId, Pageable pageable);
}
