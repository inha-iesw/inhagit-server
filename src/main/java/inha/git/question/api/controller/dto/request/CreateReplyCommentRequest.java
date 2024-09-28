package inha.git.question.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReplyCommentRequest(

        @NotNull
        @Schema(description = "댓글 인덱스", example = "1")
        Integer commentIdx,

        @NotNull
        @Size(min = 1, max = 1000)
        @Schema(description = "댓글 내용", example = "댓글 내용")
        String contents
) {
}
