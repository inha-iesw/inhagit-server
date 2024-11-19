package inha.git.bug_report.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record BugReportResponse(

        @NotNull
        @Schema(description = "버그 제보 ID", example = "1")
        Integer idx
) {
}
