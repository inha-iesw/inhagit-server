package inha.git.notice.api.controller.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import inha.git.notice.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SearchNoticesResponse(
        @NotNull
        @Schema(description = "공지사항 아이디", example = "1")
        Integer idx,

        @NotNull
        @Size(min = 1, max = 12)
        @Schema(description = "제목", example = "공지사항 제목")
        String title,

        @NotNull
        @Schema(description = "작성일", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,
        @NotNull
        SearchNoticeUserResponse searchNoticeUserResponse
) {
        @QueryProjection
        public SearchNoticesResponse(Notice notice, SearchNoticeUserResponse searchNoticeUserResponse) {
                this(
                        notice.getId(),
                        notice.getTitle(),
                        notice.getCreatedAt(),
                        searchNoticeUserResponse
                );
        }
}
