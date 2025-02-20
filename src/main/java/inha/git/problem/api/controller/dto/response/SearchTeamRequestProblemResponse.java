package inha.git.problem.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SearchTeamRequestProblemResponse(
        @NotNull
        @Schema(description = "문제 신청 팀 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "문제 신청 팀 이름", example = "팀1")
        String name,

        @NotNull
        SearchUserResponse leader,

        @NotNull
        List<SearchUserResponse> users
) {
}
