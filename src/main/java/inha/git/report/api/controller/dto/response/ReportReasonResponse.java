package inha.git.report.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReportReasonResponse(

        @NotNull
        @Schema(description = "신고 원인 ID", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "신고 원인 이름", example = "욕설")
        String name
) {
}
