package inha.git.team.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SearchTeamPostResponse(

        @NotNull
        @Schema(description = "팀 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "팀 게시글 제목", example = "팀 게시글 제목")
        String title,

        @NotNull
        @Schema(description = "팀 게시글 내용", example = "팀 게시글 내용")
        String contents,

        @NotNull
        @Schema(description = "팀 게시글 생성 날짜", example = "2021-08-01T00:00:00")
        LocalDateTime createdAt,

        @NotNull
        SearchTeamResponse team
) {
}
