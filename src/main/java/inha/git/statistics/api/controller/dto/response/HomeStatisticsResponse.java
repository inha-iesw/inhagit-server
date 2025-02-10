package inha.git.statistics.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record HomeStatisticsResponse(

        @NotNull
        @Schema(description = "학과 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "학과 이름", example = "컴퓨터공학과")
        String name,

        @NotNull
        @Schema(description = "프로젝트 수", example = "1")
        Integer projectCount,

        @NotNull
        @Schema(description = "등록된 질문 수", example = "10")
        Integer questionCount,

        @NotNull
        @Schema(description = "문제 참여자 수", example = "1")
        Integer problemCount
) {
}
