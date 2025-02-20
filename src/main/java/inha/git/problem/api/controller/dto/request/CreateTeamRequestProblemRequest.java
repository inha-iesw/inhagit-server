package inha.git.problem.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateTeamRequestProblemRequest(

        @NotNull
        @Schema(description = "문제 인덱스", example = "1")
        Integer problemIdx,

        @NotNull
        @Schema(description = "팀 인덱스", example = "1")
        Integer teamIdx
) {
}
