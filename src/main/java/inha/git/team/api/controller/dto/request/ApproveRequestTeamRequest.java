package inha.git.team.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ApproveRequestTeamRequest(
        
        @NotNull
        @Schema(description = "팀 인덱스", example = "1")
        Integer teamIdx,

        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer userIdx
) {
}
