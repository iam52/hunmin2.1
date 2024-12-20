package com.hunmin.domain.repository;

import com.hunmin.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 기본 단일 조회
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    // 중복 검사
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    // 조건 조회
    Optional<Member> findByEmailAndNickname(String email, String nickname);

    // 페이징된 전체 회원 조회
    @Query("SELECT m FROM Member m ORDER BY m.createdAt DESC")
    Page<Member> findAllMembers(Pageable pageable);

    // 팔로워 목록 조회
    @Query("SELECT m FROM Member m JOIN Follow f ON f.follower = m WHERE f.followee.memberId = :memberId")
    Page<Member> findFollowers(@Param("memberId") Long memberId, Pageable pageable);

    // 팔로잉 목록 조회
    @Query("SELECT m FROM Member m JOIN Follow f On f.followee = m WHERE f.follower.memberId = :memberId")
    Page<Member> findFollowees(@Param("memberId") Long memberId, Pageable pageable);
}
