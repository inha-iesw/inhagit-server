package inha.git.auth.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record EmailCheckRequest(
        @Email
        @NotEmpty(message = "이메일을 입력해 주세요")
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email,

        @NotNull
        @Schema(description = "인증 타입", example = "1")
        Integer type,

        @NotEmpty
        @ValidNumber
        @Size(min = 6, max = 6)
        @Schema(description = "인증 번호", example = "123456")
        String number
) {

}
