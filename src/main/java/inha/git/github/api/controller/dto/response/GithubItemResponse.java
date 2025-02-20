package inha.git.github.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GithubItemResponse(

        @Schema(description = "아이템 이름", example = "inha-git-project")
        String name,

        @Schema(description = "아이템 경로", example = "inha/git-project")
        String path,

        @Schema(description = "아이템 타입", example = "file")
        String type,

        @Schema(description = "아이템 다운로드 경로", example = "https://api.github.com/repos/Gyuhyeok99/-/contents/.gitignore?ref=master")
        String downloadUrl
) {
}
