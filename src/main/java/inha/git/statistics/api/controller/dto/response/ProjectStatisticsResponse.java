package inha.git.statistics.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectStatisticsResponse(

        @NotNull
        @Schema(description = "등록된 프로젝트 수", example = "10")
        Integer projectCount,
        @NotNull
        @Schema(description = "프로젝트를 업로드한 학생", example = "4")
        Integer userCount
) {
}
