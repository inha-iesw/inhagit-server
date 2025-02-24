package inha.git.problem.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateRequestProblemRequest(

        @NotNull
        @ValidParameter
        @Schema(description = "문제 참여 제목", example = "문제 참여 제목 수정")
        String title,

        @NotNull
        @ValidParameter
        @Schema(description = "문제 참여 팀", example = "문제 참여 팀 수정")
        String team,

        @NotNull
        @ValidParameter
        @Schema(description = "문제 참여 내용", example = "문제 참여 내용 수정")
        String contents,

        List<UpdateProblemParticipantRequest> participants
) {
}
