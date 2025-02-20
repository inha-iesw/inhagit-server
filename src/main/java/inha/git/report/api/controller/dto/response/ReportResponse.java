package inha.git.report.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReportResponse(

        @NotNull
        @Schema(description = "신고 인덱스", example = "1")
        Integer idx
) {
}
