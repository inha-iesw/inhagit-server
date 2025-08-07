package inha.git.admin.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserRankingScoreResponse(
        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer userIdx
) {
}
