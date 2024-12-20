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

    // 회원 정보 조회
    Optional<Member> findByEmail(String email);

    // 중복 체크
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    // 닉네임으로 조회
    Optional<Member> findByNickname(String nickname);

    @Query("SELECT m FROM ChatRoom c join c.member m on c.chatRoomId= :chatRoomId")
    Optional<Member> findByChatRoomId(@Param("chatRoomId") Long chatRoomId);

    // 비밀번호 찾기/변경 메서드
    Optional<Member> findUserByEmailAndNickname(String email, String nickname);
    boolean existsByEmailAndNickname(String email, String nickname);

    Optional<Member> findByNicknameAndEmail(String nickname, String email);

    //모든 멤버 리스트로 조회
    Page<Member> findAll(Pageable pageable);

}
