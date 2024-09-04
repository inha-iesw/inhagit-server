package inha.git.statistics.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProblemStatisticsResponse(

        @NotNull
        @Schema(description = "출제된 문제", example = "10")
        Integer problemCount,

        @NotNull
        @Schema(description = "문제 참여자", example = "4")
        Integer userCount
) {
}
