package inha.git.report.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReportTypeResponse(

        @NotNull
        @Schema(description = "신고 타입 ID", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "신고 타입 이름", example = "I-FOSS")
        String name
) {
}
