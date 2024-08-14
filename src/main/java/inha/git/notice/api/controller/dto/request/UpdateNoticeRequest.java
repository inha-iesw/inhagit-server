package inha.git.notice.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNoticeRequest(
        @NotNull
        @Size(min = 1, max = 32, message = "제목은 1자 이상 32자 이하로 입력해주세요.")
        @Schema(description = "제목", example = "공지사항 수정 제목")
        String title,
        @NotNull
        @Size(min = 1, message = "내용은 1자 이상 입력해주세요.")
        @Schema(description = "내용", example = "공지사항 수정 내용")
        String contents
) {
}
