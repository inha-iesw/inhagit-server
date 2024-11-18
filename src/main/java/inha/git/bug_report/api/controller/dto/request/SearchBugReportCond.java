package inha.git.bug_report.api.controller.dto.request;

import inha.git.bug_report.domain.enums.BugStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record SearchBugReportCond(

        @Schema(description = "버그 제보 제목", example = "버그 제보 제목")
        String title,

        @Schema(description = "버그 제보 상태", example = "UNCONFIRMED")
        BugStatus bugStatus
) {
}
