package inha.git.project.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(

        @NotNull
        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer projectIdx,

        @NotNull
        @Size(min = 1, max = 250, message = "내용은 1자 이상 250자 이하로 입력해주세요.")
        @Schema(description = "댓글 내용", example = "댓글 내용")
        String contents
) {
}
