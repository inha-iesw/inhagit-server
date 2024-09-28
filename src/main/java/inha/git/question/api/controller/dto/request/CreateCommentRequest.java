package inha.git.question.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotNull
        @Schema(description = "질문 인덱스", example = "1")
        Integer questionIdx,

        @NotNull
        @Size(min = 1, max = 1000)
        @ValidParameter
        @Schema(description = "댓글 내용", example = "댓글 내용")
        String contents
) {
}
