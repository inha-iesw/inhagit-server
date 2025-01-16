package inha.git.report.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateReportRequest(

        @NotNull
        @Schema(description = "신고 타입 id", example = "1")
        Integer reportTypeId,

        @NotNull
        @Schema(description = "신고 원인 id", example = "1")
        Integer reportReasonId,

        @NotNull
        @Schema(description = "신고 대상 id", example = "1")
        Integer reportedId,

        @NotNull
        @Schema(description = "신고 설명", example = "신고합니다.")
        String description
) {
}
