package inha.git.admin.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UserRankingScoreRequest(
        @NotNull(message = "유저 인덱스는 필수입니다.")
        @Schema(description = "점수를 입력할  유저 인덱스", example = "1")
        Integer userIdx,

        @Min(value = 0, message = "점수는 0 이상이어야 합니다.")
        @NotNull(message = "점수는 필수입니다.")
        @Schema(description = "Admin Score 입력", example = "10")
        Integer adminScore
) {
}
