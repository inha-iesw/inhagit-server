package inha.git.team.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TeamCommentResponse(

        @NotNull
        @Schema(description = "팀 댓글 인덱스", example = "1")
        Integer idx
) {
}
