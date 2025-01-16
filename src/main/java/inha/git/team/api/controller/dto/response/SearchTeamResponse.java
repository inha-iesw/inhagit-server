package inha.git.team.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchTeamResponse(

        @NotNull
        @Schema(description = "팀 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "팀 이름", example = "팀 이름")
        String name,

        @NotNull
        @Schema(description = "팀 최대 인원", example = "10")
        Integer maxMember,

        @NotNull
        @Schema(description = "팀 현재 인원", example = "1")
        Integer currentMember,

        @NotNull
        @Schema(description = "팀 생성일", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        SearchUserResponse leader,

        List<SearchTeamUserResponse> memberList
) {
}
