package com.hunmin.domain.service;

import com.hunmin.domain.dto.member.MemberRequest;
import com.hunmin.domain.dto.member.MemberResponse;
import com.hunmin.domain.entity.MemberLevel;
import com.hunmin.domain.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testCreateMember() {
        // Given
        MemberRequest memberRequest = new MemberRequest();
        memberRequest.setEmail("USER51@email.com");
        memberRequest.setPassword("1234");
        memberRequest.setNickname("USER51");
        memberRequest.setCountry("Thailand");
        memberRequest.setLevel(MemberLevel.BEGINNER);
        memberRequest.setImage("profile.png");

        // When
        MemberResponse response = memberService.register(memberRequest);

        // Then
        assertNotNull(response);
        assertEquals("USER51@email.com", response.getEmail());
    }
}