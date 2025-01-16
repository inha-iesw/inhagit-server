package inha.git.project.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SearchProjectCond(

        @Schema(description = "단과대 인덱스", example = "1")
        Integer collegeIdx,

        @Schema(description = "학과 인덱스", example = "1")
        Integer departmentIdx,

        @Schema(description = "학기 인덱스", example = "1")
        Integer semesterIdx,

        @Schema(description = "카테고리 인덱스", example = "1")
        Integer categoryIdx,

        @Schema(description = "분야 인덱스", example = "1")
        Integer fieldIdx,

        @Schema(description = "과목", example = "과목명")
        String subject,

        @Schema(description = "제목", example = "제목")
        String title
) {
}
