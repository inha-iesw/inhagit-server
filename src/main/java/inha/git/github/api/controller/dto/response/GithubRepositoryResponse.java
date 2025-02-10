package inha.git.github.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GithubRepositoryResponse(

        @NotNull
        @Schema(description = "깃허브 레포지토리 인덱스", example = "839135815")
        Long id,

        @NotNull
        @Schema(description = "레포지토리 이름", example = "inha-git-project")
        String name,

        @NotNull
        @Schema(description = "레포지토리 전체 이름", example = "inha/git-project")
        String fullName,

        @NotNull
        @Schema(description = "기본 브랜치 ", example = "develop")
        String defaultBranch,

        @NotNull
        @Schema(description = "레포지토리 경로", example = "https://api.github.com/repos/Gyuhyeok99/-/{archive_format}{/ref}")
        String archiveUrl
) {
}
