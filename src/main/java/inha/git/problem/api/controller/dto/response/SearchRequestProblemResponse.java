package inha.git.problem.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SearchRequestProblemResponse(
        @NotNull
        @Schema(description = "문제 신청 인덱스", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "문제 신청 타입", example = "1")
        Integer type,
        @NotNull
        @Schema(description = "문제 신청 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,
        @Schema(description = "문제 승인 날짜", example = "2021-08-01T00:00:00", nullable = true)
        LocalDateTime acceptAt,

        SearchUserRequestProblemResponse user,
        SearchTeamRequestProblemResponse team
) {
}
