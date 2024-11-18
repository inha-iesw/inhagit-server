package inha.git.notice.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record SearchNoticeResponse(
        @NotNull
        @Schema(description = "공지사항 아이디", example = "1")
        Integer idx,

        @NotNull
        @Size(min = 1, max = 12)
        @Schema(description = "제목", example = "공지사항 제목")
        String title,

        @NotNull
        @Size(min = 1)
        @Schema(description = "내용", example = "공지사항 내용")
        String contents,

        @NotNull
        @Schema(description = "첨부파일 여부", example = "true")
        Boolean hasAttachment,

        List<SearchNoticeAttachmentResponse> attachment,
        @NotNull
        @Schema(description = "작성일", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,
        @NotNull
        SearchNoticeUserResponse author
) { }
