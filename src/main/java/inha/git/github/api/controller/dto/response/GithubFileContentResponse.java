package inha.git.github.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GithubFileContentResponse(
        @Schema(description = "파일 이름", example = "README.md")
        String name,

        @Schema(description = "파일 경로", example = "inha/git-project/README.md")
        String path,

        @Schema(description = "파일 내용", example = "This is a README.md file.")
        String content,

        @Schema(description = "파일 인코딩", example = "base64")
        String encoding
) {
}
