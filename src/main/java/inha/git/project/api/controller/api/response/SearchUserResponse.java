package inha.git.project.api.controller.api.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchUserResponse(
        @NotNull
        @Schema(description = "유저 인덱스", example = "1")
        Integer idx,
        @NotNull
        @Schema(description = "유저 이름", example = "홍길동")
        String name
) {
}
