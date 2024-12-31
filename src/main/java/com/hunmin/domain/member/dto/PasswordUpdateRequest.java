package com.hunmin.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordUpdateRequest {

    @Email
    private String email;

    @NotBlank(message = "사용자 이름은 필수 입력값입니다.")
    private String nickname;

    @NotBlank(message = "새 비밀번호는 필수 입력값니다.")
    private String newPassword;

}
