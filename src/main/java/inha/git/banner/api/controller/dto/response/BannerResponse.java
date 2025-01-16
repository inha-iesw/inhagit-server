package inha.git.banner.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record BannerResponse(
        @NotNull
        @Schema(description = "배너 인덱스", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "배너 이미지 경로", example = "/banner/1716150921629-791875.jpg")
        String imgPath
) {
}
