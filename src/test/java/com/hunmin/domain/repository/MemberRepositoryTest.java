package com.hunmin.domain.repository;

import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Member member1;
    private Member member2;
    private Member member3;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(Member.builder()
                .email("member1@email.com")
                .password(bCryptPasswordEncoder.encode("1234"))
                .build()
        );

        member2 = memberRepository.save(Member.builder()
                .email("member2@email.com")
                .password(bCryptPasswordEncoder.encode("2345"))
                .nickname("member2")
                .build()
        );

        member3 = memberRepository.save(Member.builder()
                .email("member3@email.com")
                .password(bCryptPasswordEncoder.encode("3456"))
                .nickname("member3")
                .country("South America")
                .level(MemberLevel.BEGINNER)
                .memberRole(MemberRole.USER)
                .image("profile3.png")
                .build()
        );
    }

    @Test
    @DisplayName("이메일로 조회")
    void findByEmailTest() {
        // when
        Member foundMember = memberRepository.findByEmail(member1.getEmail()).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);

        // then
        assertNotNull(foundMember);
        assertEquals(member1.getEmail(), foundMember.getEmail());
    }

    @Test
    @DisplayName("닉네임으로 조회")
    void findByNicknameTest() {
        // when
        Member foundMember = memberRepository.findByNickname(member2.getNickname()).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);

        // then
        assertNotNull(foundMember);
        assertEquals(member2.getNickname(), foundMember.getNickname());
    }

    @Test
    @DisplayName("이메일 중복 검사")
    void existsByEmailTest() {
        // when & then
        assertTrue(memberRepository.existsByEmail(member3.getEmail()));
        assertFalse(memberRepository.existsByEmail("member4@email.com"));
    }

    @Test
    @DisplayName("넥네임 중복 검사")
    void existsByNicknameTest() {
        // when & then
        assertTrue(memberRepository.existsByNickname(member1.getNickname()));
        assertFalse(memberRepository.existsByNickname("member4"));
    }

    @Test
    @DisplayName("이메일과 닉네임으로 조회")
    void findByEmailAndNicknameTest() {
        // when
        Member foundMember = memberRepository.findByEmailAndNickname(member2.getEmail(), member2.getNickname())
                .orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);

        // then
        assertNotNull(foundMember);
        assertEquals(member2.getEmail(), foundMember.getEmail());
        assertEquals(member2.getNickname(), foundMember.getNickname());
    }
}