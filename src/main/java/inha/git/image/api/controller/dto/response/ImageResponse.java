package inha.git.image.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ImageResponse(
        @NotNull
        @Schema(description = "이미지 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "이미지 URL", example = "https://inha.ac.kr")
        String imageUrl
) {
}
