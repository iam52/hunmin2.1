package com.hunmin.domain.notice.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hunmin.domain.notice.entity.Notice;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NoticeResponseDTO {

    private Long noticeId;
    private Long memberId;
    private String title;
    private String content;
    private String nickname;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;


    public NoticeResponseDTO(Notice notice) {
        this.noticeId = notice.getNoticeId();
        this.memberId = notice.getMember().getMemberId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.nickname = notice.getMember().getNickname();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
    }
}
