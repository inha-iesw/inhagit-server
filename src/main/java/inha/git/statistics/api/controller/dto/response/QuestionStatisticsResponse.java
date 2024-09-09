package inha.git.statistics.api.controller.dto.response;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record QuestionStatisticsResponse(

        SearchCollegeResponse college, // 단과대 정보 정보
        SearchDepartmentResponse department, // 학과 정보

        SearchFieldResponse field, // 분야 정보

        SearchSemesterResponse semester, // 학기 정보

        @NotNull
        @Schema(description = "등록된 질문", example = "10")
        Integer questionCount,
        @NotNull
        @Schema(description = "멘토링 참여 인원", example = "4")
        Integer userCount
) {
}
