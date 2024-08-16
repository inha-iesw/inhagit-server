package inha.git.project.api.controller.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SearchReplyCommentResponse(
        @NotNull
        @Schema(description = "답글 ID", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "답글 내용", example = "답글 내용")
        String contents,

        @NotNull
        @Schema(description = "작성자 정보")
        SearchUserResponse author,

        @NotNull
        @Schema(description = "답글 생성 날짜", example = "2024-08-16T00:10:02.967995")
        LocalDateTime createdAt
) {
}
