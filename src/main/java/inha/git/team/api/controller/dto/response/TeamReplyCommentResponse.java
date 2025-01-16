package inha.git.team.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TeamReplyCommentResponse(

        @NotNull
        @Schema(description = "팀 대댓글 인덱스", example = "1")
        Integer idx
) {
}
