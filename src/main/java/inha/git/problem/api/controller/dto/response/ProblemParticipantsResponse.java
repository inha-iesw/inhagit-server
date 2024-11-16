package inha.git.problem.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchUserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ProblemParticipantsResponse(

        @NotNull
        @Schema(description = "문제 참여 인덱스", example = "1")
        Integer idx,
        @Schema(description = "문제 참여 승인 날짜", example = "2021-10-10T10:10:10.10")
        LocalDateTime acceptedAt,
        @NotNull
        @Schema(description = "문제 참여 생성 날짜", example = "2021-10-10T10:10:10.10")
        LocalDateTime createdAt,
        @NotNull
        @Schema(description = "문제 참여 타입", example = "1")
        Integer type,
        ProblemSubmitResponse submit,
        SearchUserResponse user,

        SearchTeamRequestProblemResponse team
) {
}
