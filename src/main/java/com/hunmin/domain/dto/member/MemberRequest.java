package com.hunmin.domain.dto.member;

import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\\.[A-Za-z]{2,6}$", message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,10}$", message = "닉네임은 2~10자의 한글, 영문, 숫자만 가능합니다")
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
