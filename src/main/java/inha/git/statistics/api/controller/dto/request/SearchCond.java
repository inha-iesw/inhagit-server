package inha.git.statistics.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchCond(

        @Schema(description = "단과대 인덱스", example = "1")
        Integer collegeIdx,

        @Schema(description = "학과 인덱스", example = "1")
        Integer departmentIdx,

        @Schema(description = "학기 인덱스", example = "1")
        Integer semesterIdx,

        @Schema(description = "분야 인덱스", example = "1")
        Integer fieldIdx,

        @Schema(description = "카테고리 인덱스", example = "1")
        Integer categoryIdx
) {
}
