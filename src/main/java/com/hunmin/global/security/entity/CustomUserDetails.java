package com.hunmin.global.security.entity;

import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.entity.MemberLevel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

// UserDetails를 구현하여 인증된 사용자의 정보 처리를 위한 클래스
public class CustomUserDetails implements UserDetails {

    // Member 객체로 CustomUserDetails 초기화
    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    // 사용자의 권한을 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority("ROLE_" + member.getMemberRole().name()));

        return collection;
    }

    // 사용자의 비밀번호 반환
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 사용자의 이메일 반환 (Username으로 사용)
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // 사용자의 memberId 반환
    public Long getMemberId() { return member.getMemberId(); }

    //사용자의 닉네임 반환
    public String getNickname() { return member.getNickname(); }

    //사용자의 프로필 이미지 반환
    public String getImage() { return member.getImage(); }

    //사용자의 레벨 반환
    public MemberLevel getLevel() { return member.getLevel(); }

    //사용자의 국가 반환
    public String getCountry() { return member.getCountry(); }

    // 계정이 만료되지 않았는지 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정이 잠겨 있지 않은지 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 자격 증명이 만료되지 않았는지 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 상태인지 여부 반환
    @Override
    public boolean isEnabled() {
        return true;
    }
}
