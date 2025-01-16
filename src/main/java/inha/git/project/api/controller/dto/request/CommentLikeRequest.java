package inha.git.project.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommentLikeRequest(

        @NotNull
        @Schema(description = "댓글 인덱스", example = "1")
        Integer idx
) {
}
