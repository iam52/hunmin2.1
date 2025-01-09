package com.hunmin.domain.member.service;

import com.hunmin.domain.member.dto.*;
import com.hunmin.domain.member.entity.Member;
import com.hunmin.domain.member.entity.MemberLevel;
import com.hunmin.domain.member.entity.MemberRole;
import com.hunmin.domain.member.repository.MemberRepository;
import com.hunmin.global.exception.CustomException;
import com.hunmin.global.exception.ErrorCode;
import com.hunmin.global.s3.S3FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
    private final S3FileUploader s3FileUploader;

    // 회원 가입
    public MemberResponse register(MemberRequest memberRequest) {
        if (memberRepository.existsByEmail(memberRequest.getEmail())) {
            throw ErrorCode.MEMBER_ALREADY_EXIST.throwException();
        }
        Member savedMember = memberRepository.save(Member.builder()
                .email(memberRequest.getEmail())
                .password(bCryptPasswordEncoder.encode(memberRequest.getPassword()))
                .nickname(memberRequest.getNickname())
                .country(memberRequest.getCountry())
                .memberRole(MemberRole.USER)
                .level(Optional.ofNullable(memberRequest.getLevel()).orElse(MemberLevel.BEGINNER))
                .image(memberRequest.getImage())
                .build());
        return MemberResponse.from(savedMember);
    }

    // 회원 단 건 조회
    public MemberResponse getMember(String nickname) {
        Member foundMember = memberRepository.findByNickname(nickname).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        return MemberResponse.from(foundMember);
    }

    // 회원 정보 업데이트
    public MemberResponse updateMember(Long id, MemberRequest memberRequest) {
        Member member = memberRepository.findById(id).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        if (memberRequest.getNickname() != null && !member.getNickname().equals(memberRequest.getNickname())) {
            if (memberRepository.existsByNickname(memberRequest.getNickname())) {
                throw ErrorCode.MEMBER_ALREADY_EXIST.throwException();
            }
        }
        if (memberRequest.getPassword() != null && !memberRequest.getPassword().isBlank()) {
            member.updatePassword(bCryptPasswordEncoder.encode(memberRequest.getPassword()));
        }
        if (memberRequest.getNickname() != null && !memberRequest.getNickname().isBlank()) {
            member.updateNickname(memberRequest.getNickname());
        }
        if (memberRequest.getCountry() != null && !memberRequest.getCountry().isBlank()) {
            member.updateCountry(memberRequest.getCountry());
        }
        if (memberRequest.getLevel() != null) {
            member.updateLevel(memberRequest.getLevel());
        }
        if (memberRequest.getImage() != null && !memberRequest.getImage().isBlank()) {
            member.updateImage(memberRequest.getImage());
        }
        return MemberResponse.from(member);
    }

    // 비밀번호 재설정을 위한 사용자 검증
    public ResponseEntity<?> verifyUserForPasswordReset(PasswordFindRequest passwordFindRequest) {
        return memberRepository.findByEmailAndNickname(
                        passwordFindRequest.getEmail(), passwordFindRequest.getNickname())
                .map(m -> ResponseEntity.ok("사용자 확인 완료"))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다."));
    }

    // 비밀번호 재설정
    public ResponseEntity<?> updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        try {
            Member member = memberRepository.findByEmailAndNickname(
                            passwordUpdateRequest.getEmail(), passwordUpdateRequest.getNickname())
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
            String encodedPassword = bCryptPasswordEncoder.encode(passwordUpdateRequest.getNewPassword());
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
    public MemberImageResponse uploadProfileImage(Long memberId, MultipartFile multipartFile) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        if (member.getImage() != null) {
            s3FileUploader.delete(member.getImage());
        }
        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());

        String profileImageUrl = s3FileUploader.uploadImage(multipartFile);
        member.updateImage(profileImageUrl);

        return new MemberImageResponse(
                member.getMemberId(),
                profileImageUrl,
                bufferedImage.getWidth(),
                bufferedImage.getHeight()
        );
    }

    // ChatMessage에서 사용자 확인
    public MemberRequest readUserInfo(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(ErrorCode.MEMBER_NOT_FOUND::throwException);
        return new MemberRequest(member);
    }
}
