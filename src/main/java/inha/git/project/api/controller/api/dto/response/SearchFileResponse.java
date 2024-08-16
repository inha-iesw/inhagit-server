package inha.git.project.api.controller.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchFileResponse(
        @NotNull
        @Schema(description = "파일 이름", example = "README.md")
        String name,
        @NotNull
        @Schema(description = "파일 타입(file or directory)", example = "file")
        String type
) {
}
