package inha.git.problem.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateRequestProblemRequest(
        @NotNull
        @Schema(description = "문제 인덱스", example = "1")
        Integer problemIdx

) {
}
