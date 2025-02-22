package inha.git.problem.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateRequestProblemRequest(

        @NotNull
        @Schema(description = "문제 인덱스", example = "1")
        Integer problemIdx,

        @NotNull
        @ValidParameter
        @Schema(description = "문제 참여 제목", example = "문제 참여 제목")
        String title,

        @NotNull
        @ValidParameter
        @Schema(description = "문제 참여 내용", example = "문제 참여 내용")
        String contents,

        List<CreateProblemParticipantRequest> participants
) {
}
