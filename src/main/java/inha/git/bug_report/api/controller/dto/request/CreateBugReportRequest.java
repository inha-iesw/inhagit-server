package inha.git.bug_report.api.controller.dto.request;

import inha.git.bug_report.domain.enums.ReportType;
import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBugReportRequest(

        @NotNull(message = "제목을 입력해주세요.")
        @Size(min = 1, max = 200)
        @ValidParameter
        @Schema(description = "버그 제보 제목", example = "버그 제보 제목")
        String title,

        @NotNull(message = "내용을 입력해주세요.")
        @ValidParameter
        @Schema(description = "버그 제보 내용", example = "버그 제보 내용")
        String contents,

        @NotNull(message = "유형을 선택해주세요.")
        @Schema(description = "제보 유형", example = "BUG")
        ReportType reportType
) {
}
