package inha.git.auth.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidEmail;
import inha.git.common.validation.annotation.ValidNumber;
import inha.git.common.validation.annotation.ValidParameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record FindPasswordCheckRequest(


        @Email
        @ValidEmail
        @ValidParameter
        @NotEmpty(message = "이메일을 입력해 주세요")
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String email,

        @NotEmpty
        @ValidNumber
        @Size(min = 6, max = 6)
        @Schema(description = "인증 번호", example = "123456")
        String number

) {
}
