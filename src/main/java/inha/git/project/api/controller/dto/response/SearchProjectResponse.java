package inha.git.project.api.controller.dto.response;

import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchProjectResponse(

        @NotNull
        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "프로젝트 제목", example = "프로젝트 제목")
        String title,

        @NotNull
        @Schema(description = "프로젝트 내용", example = "프로젝트 내용")
        String contents,

        @NotNull
        @Schema(description = "프로젝 생성 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        List<SearchFieldResponse> fieldList,

        @NotNull
        SearchUserResponse author,

        @NotNull
        @Schema(description = "프로젝트 파일 경로", example = "/project/1723085103043-875309")
        String filePath,

        @NotNull
        @Schema(description = "프로젝트 zip 파일 경로", example = "/project-zip/1723085103043-875309.zip")
        String zipFilePath,

        @NotNull
        SearchRecommendState recommendState,

        @NotNull
        SearchRecommendCount recommendCount,

        @Schema(description = "프로젝트 레포지토리 이름", example = "project-repo", nullable = true)
        String repoName,

        @NotNull
        @Schema(description = "프로젝트 주제 이름", example = "project-subject")
        String subject,

        @NotNull
        SearchSemesterResponse semester,

        @NotNull
        @Schema(description = "프로젝트 공개 여부", example = "true")
        Boolean isPublic,

        @NotNull
        SearchCategoryResponse category,

        List<SearchPatentSummaryResponse> patent
) {
}
