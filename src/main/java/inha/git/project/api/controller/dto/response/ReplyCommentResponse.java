package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ReplyCommentResponse(

        @NotNull
        @Schema(description = "프로젝트 대댓글 인덱스", example = "1")
        Integer idx
) {
}
