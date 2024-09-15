package inha.git.project.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CommentWithRepliesResponse(
        @NotNull
        @Schema(description = "댓글 ID", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "댓글 내용", example = "댓글 내용")
        String contents,

        @NotNull
        @Schema(description = "작성자 정보")
        SearchUserResponse author,

        @NotNull
        @Schema(description = "댓글 생성 날짜", example = "2024-08-16T00:10:02.967995")
        LocalDateTime createdAt,

        @NotNull
        @Schema(description = "댓글 좋아요 개수", example = "1")
        Integer likeCount,

        @NotNull
        @Schema(description = "답글 목록")
        List<SearchReplyCommentResponse> replies
) {}