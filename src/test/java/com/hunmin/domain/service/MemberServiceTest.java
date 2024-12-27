package com.hunmin.domain.service;

import com.hunmin.domain.dto.member.MemberRequest;
import com.hunmin.domain.dto.member.MemberResponse;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    @DisplayName("회원 가입 성공")
    void registerTest() {
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setEmail("test@test.com");
        memberRequest.setPassword(bCryptPasswordEncoder.encode("123456"));

        // when
        MemberResponse memberResponse = memberService.register(memberRequest);

        // then
        assertNotNull(memberResponse);
        assertEquals(memberResponse.getEmail(), memberRequest.getEmail());
    }

    @Test
    @DisplayName("중복 이메일 가입 실패")
    void registerDuplicateEmailTest() {
        // given
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setEmail("test@test.com");
        memberRequest.setPassword(bCryptPasswordEncoder.encode("123456"));
        memberRequest.setNickname("testMan");

        // when
        memberService.register(memberRequest);

        // then
        assertThrows(CustomException.class, () -> memberService.register(memberRequest));
    }
}