package inha.git.bug_report.api.controller.dto.response;

import inha.git.bug_report.domain.enums.BugStatus;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SearchBugReportsResponse(

        @NotNull
        @Schema(description = "버그 제보 ID", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "버그 제보 제목", example = "버그 제보 제목")
        String title,

        @NotNull
        @Schema(description = "질문 생성 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        @Schema(description = "버그 상태", example = "UNCONFIRMED")
        BugStatus bugStatus,

        @NotNull
        SearchUserResponse author
) {
}
