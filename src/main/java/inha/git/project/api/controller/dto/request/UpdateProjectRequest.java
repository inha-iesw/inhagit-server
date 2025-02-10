package inha.git.project.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateProjectRequest(

        @NotNull(message = "제목을 입력해주세요.")
        @Size(min = 1, max = 200)
        @ValidParameter
        @Schema(description = "프로젝트 제목", example = "프로젝트 제목")
        String title,

        @NotNull(message = "내용을 입력해주세요.")
        @ValidParameter
        @Schema(description = "프로젝트 내용", example = "프로젝트 내용")
        String contents,

        @NotNull(message = "프로젝트 주제를 작성해주세요.")
        @Size(min = 1, max = 30)
        @ValidParameter
        @Schema(description = "프로젝트 주제", example = "프로젝트 주제")
        String subject,

        @NotNull(message = "분야를 선택해주세요.")
        @Size(min = 1, max = 1, message = "하나의 분야만 선택해야 합니다.")
        List<Integer>fieldIdxList,

        @NotNull
        @Schema(description = "학기 인덱스", example = "1")
        Integer semesterIdx,

        @NotNull
        @Schema(description = "카테고리 인덱스", example = "1")
        Integer categoryIdx,

        @NotNull
        @Schema(description = "공개 여부", example = "true")
        Boolean isPublic
) {
}
