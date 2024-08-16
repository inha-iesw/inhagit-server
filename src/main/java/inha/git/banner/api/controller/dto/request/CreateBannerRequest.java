package inha.git.banner.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateBannerRequest(
        @NotNull(message = "배너 파일을 업로드해주세요.")
        @Schema(description = "배너 파일", example = "banner.jpg")
        MultipartFile file
) {
}
