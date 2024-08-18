package inha.git.question.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommentResponse(
        @NotNull
        @Schema(description = "질문 댓글 인덱스", example = "1")
        Integer idx
) {
}
