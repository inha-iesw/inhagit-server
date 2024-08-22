package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record RequestProblemResponse(
        @NotNull
        @Schema(description = "문제 참여 인덱스", example = "1")
        Integer idx
) {
}
