package com.hunmin.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@JsonIgnoreProperties(ignoreUnknown = true) //리액트에서 필요한 데이터만 보내는걸로 수정했지만, 백엔드에서도 이중으로 불필요한 데이터 무시

public class NoticeUpdateDTO {


    @NotEmpty(message = "제목을 입력하세요")
    private String title;
    @NotEmpty(message = "내용을 입력하세요")
    private String content;





}