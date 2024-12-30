package com.hunmin.domain.service;

import com.hunmin.domain.dto.member.MemberRequest;
import com.hunmin.domain.dto.member.MemberResponse;
import com.hunmin.domain.dto.member.PasswordFindRequestDto;
import com.hunmin.domain.dto.member.PasswordUpdateRequestDto;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.repository.MemberRepository;
import com.hunmin.global.exception.CustomException;
import com.hunmin.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 회원 가입
    public MemberResponse register(MemberRequest memberRequest) {
        String email = memberRequest.getEmail();
        String password = memberRequest.getPassword();
        boolean isEmailRegistered = memberRepository.existsByEmail(email);
        if (isEmailRegistered) {
            throw ErrorCode.MEMBER_ALREADY_EXIST.throwException();
        }
        MemberLevel memberLevel = memberRequest.getLevel() != null ? memberRequest.getLevel() : MemberLevel.BEGINNER;
        Member member = Member.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname(memberRequest.getNickname())
                .country(memberRequest.getCountry())
                .memberRole(MemberRole.USER)
                .level(memberLevel)
                .image(memberRequest.getImage())
                .build();
        Member savedMember = memberRepository.save(member);
        return MemberResponse.from(savedMember);
    }

    // 회원 단 건 조회
    public MemberResponse getMember(String nickname) {
        Member foundMember = memberRepository.findByNickname(nickname).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        return MemberResponse.from(foundMember);
    }

    // 회원 정보 업데이트
    public void updateMember(Long id, MemberRequest memberRequest) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            if (memberRequest.getPassword() != null && !memberRequest.getPassword().isEmpty()) {
                member.updatePassword(bCryptPasswordEncoder.encode(memberRequest.getPassword()));
            }
            if (memberRequest.getNickname() != null) {
                member.updateNickname(memberRequest.getNickname());
            }
            if (memberRequest.getCountry() != null) {
                member.updateCountry(memberRequest.getCountry());
            }
            if (memberRequest.getLevel() != null) {
                member.updateLevel(memberRequest.getLevel());
            }
            if (memberRequest.getImage() != null) {
                member.updateImage(memberRequest.getImage());
            }
            memberRepository.save(member);
        } else {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }
    }

    // 비밀번호 재설정을 위한 사용자 검증
    public ResponseEntity<?> verifyUserForPasswordReset(PasswordFindRequestDto passwordFindRequestDto) {
        return memberRepository.findByEmailAndNickname(
                        passwordFindRequestDto.getEmail(), passwordFindRequestDto.getNickname())
                .map(m -> ResponseEntity.ok("사용자 확인 완료"))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 재설정
    public ResponseEntity<?> updatePassword(PasswordUpdateRequestDto passwordUpdateRequestDto) {
        try {
            Member member = memberRepository.findByEmailAndNickname(
                            passwordUpdateRequestDto.getEmail(), passwordUpdateRequestDto.getNickname())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            String encodedPassword = bCryptPasswordEncoder.encode(passwordUpdateRequestDto.getNewPassword());
            member.updatePassword(encodedPassword);
            memberRepository.save(member);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 이미지 업로드
    public String uploadImage(MultipartFile file) throws IOException {
        String uploadDir = Paths.get("uploads").toAbsolutePath().normalize().toString();
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory");
            }
        }
        String fileName = UUID.randomUUID() + "." + getFileExtension(file.getOriginalFilename());
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);
        return "/uploads/" + fileName;
    }

    // 파일 확장자 추출
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public MemberRequest readUserInfo(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        if (member == null) {
            throw new CustomException((ErrorCode.MEMBER_NOT_FOUND));
        }
        return new MemberRequest(member);
    }
}
