package inha.git.project.api.controller.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record DeleteProjectResponse(
        @NotNull
        @Schema(description = "프로젝트 인덱스", example = "1")
        Integer idx
) {
}
