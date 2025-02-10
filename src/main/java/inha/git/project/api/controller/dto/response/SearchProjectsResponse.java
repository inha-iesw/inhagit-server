package inha.git.project.api.controller.dto.response;

import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchProjectsResponse(

        @NotNull
        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "프로젝트 제목", example = "프로젝트 제목")
        String title,

        @NotNull
        @Schema(description = "프로젝트 생성 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        @Schema(description = "true면 깃허브 false면 사용자 업로드 프로젝트", example = "true")
        Boolean isRepo,

        @NotNull
        SearchSemesterResponse semester,

        @NotNull
        SearchCategoryResponse category,

        @NotNull
        @Schema(description = "과목", example = "클라우드컴퓨팅")
        String subject,

        @NotNull
        @Schema(description = "좋아요 수", example = "1")
        Integer likeCount,

        @NotNull
        @Schema(description = "댓글 수", example = "1")
        Integer commentCount,

        @NotNull
        @Schema(description = "공개 여부", example = "true")
        Boolean isPublic,

        @NotNull
        List<SearchFieldResponse> fieldList,

        @NotNull
        SearchUserResponse author,

        SearchPatentSummaryResponse patent
) {
}
