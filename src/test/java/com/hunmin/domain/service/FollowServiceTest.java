package com.hunmin.domain.service;

import com.hunmin.domain.follow.service.FollowService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
public class FollowServiceTest {

    @Autowired
    private FollowService followService;

    @Test
    @Commit
    public void 팔로이등록(){
        //given
        String email = "dom10461@gmail.com";
        Long memberId = 2L;
        //when
        followService.register(email, memberId);
        //then
    }
    @Test
    @Commit
    public void 팔로이삭제(){
        //given
        Long memberId = 1L;
        String email = "dom10461@gmail.com";
        //when
        followService.remove(email,memberId);
    }
    @Test
    public void 팔로우목록보기(){
    }
}
