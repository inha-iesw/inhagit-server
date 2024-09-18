package inha.git.question.api.controller.dto.response;

import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record SearchQuestionResponse(
        @NotNull
        @Schema(description = "질문 인덱스", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "질문 제목", example = "질문 제목")
        String title,
        @NotNull
        @Schema(description = "질문 내용", example = "질문 내용")
        String contents,

        @NotNull
        @Schema(description = "질문 생성 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        SearchLikeState likeState,

        @NotNull
        @Schema(description = "질문 좋아요 개수", example = "1")
        Integer likeCount,

        @NotNull
        @Schema(description = "질문 주제", example = "질문 주제")
        String subject,
        @NotNull
        List<SearchFieldResponse> fieldList,
        @NotNull
        SearchUserResponse author,

        @NotNull
        SearchSemesterResponse semester
) {
}
