package inha.git.project.api.controller.dto.response;

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
        List<SearchFieldResponse> fieldList,
        @NotNull
        SearchUserResponse author
) {
}
