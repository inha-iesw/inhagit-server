package inha.git.statistics.api.controller.dto.response;

import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SemesterStatistics(

        SearchSemesterResponse semester,

        @NotNull
        @Schema(description = "전체 프로젝트 수", example = "8")
        Integer totalProjectCount,

        @NotNull
        @Schema(description = "로컬로 등록된 프로젝트 수", example = "4")
        Integer localProjectCount,

        @NotNull
        @Schema(description = "깃허브로 등록된 프로젝트 수", example = "4")
        Integer githubProjectCount,

        @NotNull
        @Schema(description = "프로젝트에 등록된 특허 수", example = "4")
        Integer patentProjectCount,

        @NotNull
        @Schema(description = "등록된 질문", example = "10")
        Integer questionCount
) {
}
