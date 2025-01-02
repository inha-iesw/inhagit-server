package inha.git.notice.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record SearchNoticeAttachmentResponse(

        @NotNull
        @Schema(description = "첨부파일 아이디", example = "1")
        Integer idx,

        @NotNull
        @Schema(description = "첨부파일 원본 파일 이름", example = "첨부파일.jpg")
        String originalFileName,

        @NotNull
        @Schema(description = "첨부파일 저장 파일 이름", example = "123456789.jpg")
        String storedFileUrl
) {
}
