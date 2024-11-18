package inha.git.report.api.controller.dto.response;

import inha.git.common.BaseEntity.State;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SearchReportResponse(

        @NotNull
        @Schema(description = "신고 인덱스", example = "1")
        Integer idx,

        ReportTypeResponse reportType,

        ReportReasonResponse reportReason,

        SearchUserResponse reporter,

        SearchUserResponse reported,

        @Schema(description = "신고 내용", example = "신고 내용")
        String description,

        @NotNull
        @Schema(description = "신고 상태", example = "ACTIVE")
        State state,

        @NotNull
        @Schema(description = "신고 일자", example = "2021-08-01")
        LocalDateTime createdAt,

        @Schema(description = "취소 일자", example = "2021-08-01")
        LocalDateTime deletedAt
) {
}
