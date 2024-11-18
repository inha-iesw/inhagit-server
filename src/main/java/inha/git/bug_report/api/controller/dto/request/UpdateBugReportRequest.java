package inha.git.bug_report.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateBugReportRequest(

        @NotNull(message = "제목을 입력해주세요.")
        @Size(min = 1, max = 200)
        @ValidParameter
        @Schema(description = "버그 제보 제목 수정", example = "버그 제보 제목 수정")
        String title,

        @NotNull(message = "내용을 입력해주세요.")
        @ValidParameter
        @Schema(description = "버그 제보 내용 수정", example = "버그 제보 내용 수정")
        String contents
) {
}
