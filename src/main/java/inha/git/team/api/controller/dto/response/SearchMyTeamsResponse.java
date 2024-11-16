package inha.git.team.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SearchMyTeamsResponse(
        @NotNull
        @Schema(description = "팀 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "팀 이름", example = "팀 이름")
        String name,

        @NotNull
        @Schema(description = "팀 생성 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        @Schema(description = "가입 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime joinedAt,

        @NotNull
        SearchUserResponse leader
) {
}
