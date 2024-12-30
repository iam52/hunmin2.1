package com.hunmin.global.security;

import com.hunmin.domain.entity.Member;
import com.hunmin.domain.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member memberData = memberRepository.findByEmail(email).get();

        if (memberData != null) {
            return new CustomUserDetails(memberData);
        }
        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
    }
}
