package com.hunmin.domain.service;

import com.hunmin.domain.dto.member.MemberDTO;
import com.hunmin.domain.dto.member.PasswordFindRequestDto;
import com.hunmin.domain.dto.member.PasswordUpdateRequestDto;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.entity.MemberLevel;
import com.hunmin.domain.entity.MemberRole;
import com.hunmin.domain.jwt.JWTUtil;
import com.hunmin.domain.repository.MemberRepository;
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
    public void registerProcess(MemberDTO memberDTO) {
        String email = memberDTO.getEmail();
        String password = memberDTO.getPassword();

        boolean isExist = memberRepository.existsByEmail(email);
        if (isExist) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        MemberLevel selectedLevel = memberDTO.getLevel() != null ? memberDTO.getLevel() : MemberLevel.BEGINNER;

        Member member = Member.builder()
                .email(email)
                .password(bCryptPasswordEncoder.encode(password))
                .nickname(memberDTO.getNickname())
                .country(memberDTO.getCountry())
                .memberRole(MemberRole.USER)
                .level(selectedLevel)
                .image(memberDTO.getImage())
                .build();

        memberRepository.save(member);
    }
    // 회원 정보 업데이트
    public void updateMember(Long id, MemberDTO memberDTO) {
        Optional<Member> optionalMember = memberRepository.findById(id);

        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();

            if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
                member.setPassword(bCryptPasswordEncoder.encode(memberDTO.getPassword()));
            }
            if (memberDTO.getNickname() != null) {
                member.setNickname(memberDTO.getNickname());
            }
            if (memberDTO.getCountry() != null) {
                member.setCountry(memberDTO.getCountry());
            }
            if (memberDTO.getLevel() != null) {
                member.setLevel(memberDTO.getLevel());
            }

            if(memberDTO.getImage() != null) {
                member.setImage(memberDTO.getImage());
            }

            memberRepository.save(member);
        } else {
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }
    }
    public MemberDTO readUserInfo(String email) {
        return new MemberDTO(memberRepository.findByEmail(email));
    }

    // 비밀번호 재설정을 위한 사용자 검증

    public ResponseEntity<?> verifyUserForPasswordReset(PasswordFindRequestDto passwordFindRequestDto) {
        boolean userExists = memberRepository.existsByEmailAndNickname(
                passwordFindRequestDto.getEmail(), passwordFindRequestDto.getNickname());

        if (userExists) {
            return ResponseEntity.ok("사용자 확인 완료");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }
    }
    // 비밀번호 재설정

    public ResponseEntity<?> updatePassword(PasswordUpdateRequestDto passwordUpdateRequestDto) {
        try {
            Member member = memberRepository.findUserByEmailAndNickname(
                            passwordUpdateRequestDto.getEmail(), passwordUpdateRequestDto.getNickname())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            String encodedPassword = bCryptPasswordEncoder.encode(passwordUpdateRequestDto.getNewPassword());

            member.setPassword(encodedPassword);
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
}
