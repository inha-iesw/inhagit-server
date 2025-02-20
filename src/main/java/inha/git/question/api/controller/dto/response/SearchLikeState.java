package inha.git.question.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchLikeState(

        @NotNull
        @Schema(description = "like 상태", example = "fasle")
        Boolean like
) {
}
