package inha.git.statistics.api.controller.dto.response;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ProjectStatisticsResponse(

        SearchCollegeResponse college, // 단과대 정보 정보
        SearchDepartmentResponse department, // 학과 정보

        SearchFieldResponse field, // 분야 정보

        SearchSemesterResponse semester, // 학기 정보
        SearchCategoryResponse category, // 카테고리 정보
        @NotNull
        @Schema(description = "전체 프로젝트 수", example = "8")
        Integer totalProjectCount, // 전체 프로젝트 수

        @NotNull
        @Schema(description = "로컬로 등록된 프로젝트 수", example = "4")
        Integer localProjectCount, // 로컬  프로젝트 수

        @NotNull
        @Schema(description = "깃허브로 등록된 프로젝트 수", example = "4")
        Integer githubProjectCount, // 깃허브 프로젝트 수

        @NotNull
        @Schema(description = "프로젝트를 업로드한 학생", example = "4")
        Integer projectUserCount

) {
}
