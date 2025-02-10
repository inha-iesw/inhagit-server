package inha.git.statistics.api.controller.dto.response;

import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PatentStatisticsResponse(

        SearchSemesterResponse semester, // 학기 정보

        @NotNull
        @Schema(description = "등록된 특허 및 프로그램 수", example = "10")
        Integer patentCount

) {
}
