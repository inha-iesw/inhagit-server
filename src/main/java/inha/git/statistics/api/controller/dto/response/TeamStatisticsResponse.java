package inha.git.statistics.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record TeamStatisticsResponse(

        @NotNull
        @Schema(description = "생성된 팀", example = "10")
        Integer teamCount,

        @NotNull
        @Schema(description = "팀 가입자", example = "4")
        Integer userCount
) {
}
