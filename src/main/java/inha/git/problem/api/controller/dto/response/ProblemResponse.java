package inha.git.problem.api.controller.dto.response;

import inha.git.problem.domain.enums.ProblemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProblemResponse(
        @NotNull
        @Schema(description = "문제 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "문제 상태", example = "PROGRESS")
        ProblemStatus status
) {
}
