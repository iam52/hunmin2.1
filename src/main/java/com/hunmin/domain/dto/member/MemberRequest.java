package com.hunmin.domain.dto.member;

import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    private String nickname;

    private String country;

    private String image;

    private MemberLevel level;

    public MemberRequest(Member member) {
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.country = member.getCountry();
        this.image = member.getImage();
        this.level = member.getLevel();
    }
}
