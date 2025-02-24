package inha.git.problem.api.controller.dto.response;

import inha.git.problem.domain.enums.ProblemRequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchRequestProblemResponse(

        @NotNull
        @Schema(description = "문제 신청 인덱스", example = "1")
        int idx,

        @NotNull
        @Schema(description = "문제 신청 제목", example = "문제 신청")
        String title,

        @NotNull
        @Schema(description = "문제 신청 팀", example = "팀")
        String team,

        @NotNull
        @Schema(description = "문제 신청 내용", example = "문제 신청 내용")
        String contents,

        @Schema(description = "원본 파일명", example = "test.txt")
        String originalFileName,

        @Schema(description = "저장 파일 URL", example = "problem-request/~~~")
        String storedFileUrl,

        @NotNull
        @Schema(description = "문제 신청 상태", example = "REQUEST")
        ProblemRequestStatus problemRequestStatus,

        SearchUserRequestProblemResponse user,

        List<ProblemParticipantResponse> participants,

        @NotNull
        @Schema(description = "문제 신청 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt
) {
}
