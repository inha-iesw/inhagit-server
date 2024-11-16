package inha.git.problem.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record SearchProblemResponse(
        @NotNull
        @Schema(description = "문제 아이디", example = "1")
        Integer idx,

        @NotNull
        @Size(min = 1, max = 12)
        @Schema(description = "제목", example = "공지사항 제목")
        String title,

        @NotNull
        @Size(min = 1, max = 250)
        @Schema(description = "내용", example = "공지사항 내용")
        String contents,
        @NotNull
        @Schema(description = "작성일", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,
        @NotNull
        SearchUserResponse author,

        @NotNull
        @Schema(description = "문제 제출 기한", example = "2025-08-31")
        String duration,

        @NotNull
        @Schema(description = "문제 파일 경로", example = "/problem-file/1722397992636-372869.pdf")
        String filePath

) {
}
