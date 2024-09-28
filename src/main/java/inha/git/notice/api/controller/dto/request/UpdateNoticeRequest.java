package inha.git.notice.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNoticeRequest(
        @NotNull
        @Size(min = 1, max = 200)
        @Schema(description = "제목", example = "공지사항 수정 제목")
        String title,
        @NotNull
        @Size(min = 1, max = 3000)
        @Schema(description = "내용", example = "공지사항 수정 내용")
        String contents
) {
}
