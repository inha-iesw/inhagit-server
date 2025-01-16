package inha.git.team.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchTeamPostTeamResponse(

        @NotNull
        @Schema(description = "팀 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "팀 이름", example = "팀 이름")
        String name,

        @NotNull
        SearchUserResponse leader
) {
}
