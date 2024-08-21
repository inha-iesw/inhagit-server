package inha.git.team.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTeamPostRequest(
        @NotNull
        @Size(min = 1, max = 32, message = "제목은 1자 이상 32자 이하로 입력해주세요.")
        @Schema(description = "게시글 제목", example = "게시글 제목")
        String title,
        @NotNull
        @Size(min = 1, max = 250, message = "내용은 1자 이상 250자 이하로 입력해주세요.")
        @Schema(description = "게시글 내용", example = "게시글 내용")
        String contents,
        @NotNull
        @Schema(description = "팀 번호", example = "1")
        Integer teamIdx
) {
}
