package com.hunmin.domain.repository;

import com.hunmin.domain.follow.entity.Follow;
import com.hunmin.domain.follow.repository.FollowRepository;
import com.hunmin.domain.member.repository.MemberRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
public class FollowRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FollowRepository followRepository;

    @Test
    public void 팔로우추가() {
        //give
        Follow follow = Follow.builder()
                .follower(memberRepository.findById(1L).get())
                .followee(memberRepository.findById(2L).get())
                .isBlock(false)
                .notification(true)
                .build();
        followRepository.save(follow);

        //then
        Assertions.assertNotNull(follow);
        Assertions.assertEquals(follow.getFollower().getNickname(),memberRepository.findById(1L).get().getNickname() );
    }
}
