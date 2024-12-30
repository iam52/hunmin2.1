package com.hunmin.domain.follow.repository;

import com.hunmin.domain.follow.entity.Follow;
import com.hunmin.domain.follow.repository.search.FollowSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowSearch {
    // 팔로우 유무 확인
    @Query("SELECT f FROM Follow f WHERE f.follower.memberId = :myId AND f.followee.memberId = :memberId")
    Optional<Follow> findByMemberId(@Param("myId") Long myId
                                    ,@Param("memberId") Long memberId);

    boolean existsByFollower_MemberIdAndFollowee_MemberId(Long followerId, Long followeeId);
}
