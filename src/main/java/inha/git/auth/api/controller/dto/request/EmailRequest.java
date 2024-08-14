package inha.git.auth.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(
        @Email
        @NotEmpty(message = "이메일을 입력해 주세요")
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email,

        @NotNull
        @Schema(description = "인증 타입", example = "1")
        Integer type
) {

}
