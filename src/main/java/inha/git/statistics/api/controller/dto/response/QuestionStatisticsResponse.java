package inha.git.statistics.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record QuestionStatisticsResponse(

        @NotNull
        @Schema(description = "등록된 질문", example = "10")
        Integer questionCount,
        @NotNull
        @Schema(description = "멘토링 참여 인원", example = "4")
        Integer userCount
) {
}
