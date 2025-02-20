package inha.git.admin.api.controller.dto.request;

import inha.git.bug_report.domain.enums.BugStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ChangeBugReportStateRequest(

        @NotNull
        @Schema(description = "버그 상태", example = "CONFIRMED")
        BugStatus bugStatus
) {
}
