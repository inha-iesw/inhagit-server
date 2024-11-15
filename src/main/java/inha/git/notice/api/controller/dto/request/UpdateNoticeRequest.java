package inha.git.notice.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNoticeRequest(
        @NotNull
        @Size(min = 1, max = 200)
        @ValidParameter
        @Schema(description = "제목", example = "공지사항 수정 제목")
        String title,
        @NotNull
        @ValidParameter
        @Schema(description = "내용", example = "공지사항 수정 내용")
        String contents
) {
}
