package inha.git.project.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateReplyCommentRequest(

        @NotNull
        @Size(min = 1, max = 1000)
        @ValidParameter
        @Schema(description = "대댓글 내용 수정", example = "대댓글 수정")
        String contents
) {
}
