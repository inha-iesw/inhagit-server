package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProblemResponse(
        @NotNull
        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer idx
) {
}
