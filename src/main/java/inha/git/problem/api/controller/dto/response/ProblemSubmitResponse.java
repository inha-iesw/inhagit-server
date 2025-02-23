package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProblemSubmitResponse(

        @NotNull
        @Schema(description = "제출한 문제의 인덱스", example = "1")
        int idx
) {
}
