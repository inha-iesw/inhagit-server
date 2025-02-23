package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchProblemSubmitResponse(

        @Schema(description = "문제 제출 인덱스", example = "1")
        int idx,

        @Schema(description = "문제 인덱스", example = "1")
        int problemidx,

        @Schema(description = "문제 신청 인덱스", example = "1")
        int problemRequestidx,

        @Schema(description = "제출 프로젝트 인덱스", example = "1")
        int projectidx
) {
}
