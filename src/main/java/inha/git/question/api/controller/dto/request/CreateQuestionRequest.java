package inha.git.question.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateQuestionRequest(
        @NotNull(message = "제목을 입력해주세요.")
        @Size(min = 1, max = 200)
        @ValidParameter
        @Schema(description = "질문 제목", example = "질문 제목")
        String title,
        @NotNull(message = "내용을 입력해주세요.")
        @ValidParameter
        @Schema(description = "질문 내용", example = "질문 내용")
        String contents,
        @NotNull(message = "프로젝트 주제를 작성해주세요.")
        @Size(min = 1, max = 30)
        @ValidParameter
        @Schema(description = "질문 주제", example = "질문 주제")
        String subject,
        @NotNull(message = "분야를 선택해주세요.")
        @Size(min = 1, max = 1, message = "하나의 분야만 선택해야 합니다.")
        List<Integer> fieldIdxList,

        @NotNull
        @Schema(description = "학기 인덱스", example = "1")
        Integer semesterIdx

) {
}
