package com.hunmin.global.s3;

import com.hunmin.global.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileValidate {
    // 허용 확장자 설정
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    // 최대 파일 크기 설정: 3mb, 메모리 효율/가독성 등을 고려 상수로 설정
    private static final long MAX_FILE_SIZE = 3_145_728L;

    // 파일 검증
    public static void validateImageFile(MultipartFile file) {
        // 파일이 존재하는지 검증
        if (file.isEmpty()) {
            throw ErrorCode.IMAGE_EMPTY_FILE.throwException();
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            throw ErrorCode.IMAGE_FILE_TOO_LARGE.throwException();
        }

        // 파일명이 null인 경우 예외 발생
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw ErrorCode.IMAGE_INVALID_FILE_NAME.throwException();
        }

        // 파일의 '.' 이후 확장자 부분 소문자로 추출
        String extension = getExtension(originalFilename).toLowerCase();
        // 추출한 확장자가 없다면 예외 발생
        if (extension.isEmpty()) {
            throw ErrorCode.IMAGE_NOT_EXISTS_FILE_EXTENSION.throwException();
        }

        // IMAGE_EXTENSIONS에서 지정한 확장자인지 확인
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw ErrorCode.IMAGE_NOT_SUPPORT_FILE_EXTENSION.throwException();
        }

        // contentType 확인
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw ErrorCode.IMAGE_INVALID_FILE_TYPE.throwException();
        }
    }

    public static String createUniqueFileName(String originalFilename) {
        String extension = getExtension(originalFilename);
        return UUID.randomUUID() + "." + extension;
    }

    // 확장자 추출을 위한 헬퍼 메소드
    private static String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
}
