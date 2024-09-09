package inha.git.question.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateQuestionRequest(
        @NotNull(message = "제목을 입력해주세요.")
        @Size(min = 1, max = 32, message = "제목은 1자 이상 32자 이하로 입력해주세요.")
        @Schema(description = "질문 제목 수정", example = "질문 제목 수정")
        String title,
        @NotNull(message = "내용을 입력해주세요.")
        @Size(min = 1, message = "내용은 1자 이상 입력해주세요.")
        @Schema(description = "질문 내용 수정", example = "질문 내용 수정")
        String contents,
        @NotNull(message = "프로젝트 주제를 작성해주세요.")
        @Size(min = 1, max = 32, message = "프로젝트 주제는 1자 이상 32자 이하로 입력해주세요.")
        @Schema(description = "질문 주제 수정", example = "질문 주제 수정")
        String subject,
        @NotNull(message = "분야를 선택해주세요.")
        @Size(min = 1, max = 1, message = "하나의 분야만 선택해야 합니다.")
        List<Integer> fieldIdxList,

        @NotNull
        @Schema(description = "학기 인덱스", example = "1")
        Integer semesterIdx
) {
}
