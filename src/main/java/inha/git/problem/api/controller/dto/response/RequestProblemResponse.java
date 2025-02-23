package inha.git.problem.api.controller.dto.response;

import inha.git.problem.domain.enums.ProblemRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RequestProblemResponse(
        @NotNull
        @Schema(description = "문제 참여 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "문제 신청 상태", example = "REQUEST")
        ProblemRequestStatus problemRequestStatus
) {
}
