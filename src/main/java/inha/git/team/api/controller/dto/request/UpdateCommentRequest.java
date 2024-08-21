package inha.git.team.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCommentRequest(
        @NotNull
        @Schema(description = "팀 게시글 인덱스", example = "1")
        Integer postIdx,

        @NotNull
        @Size(min = 1, max = 250, message = "내용은 1자 이상 250자 이하로 입력해주세요.")
        @Schema(description = "댓글 내용 수정", example = "댓글 내용 수정")
        String contents
) {
}
