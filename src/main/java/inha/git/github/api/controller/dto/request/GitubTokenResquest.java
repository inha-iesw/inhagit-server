package inha.git.github.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record GitubTokenResquest(
        @NotNull
        @Schema(description = "깃허브 토큰", example = "ghp_1234567890")
        String githubToken
) {
}
