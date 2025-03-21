package inha.git.auth.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidEmail;
import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(

        @NotNull
        @Email
        @ValidEmail
        @ValidParameter
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email,

        @NotNull
        @ValidParameter
        @Schema(description = "비밀번호", example = "password2@")
        String password) {
}
