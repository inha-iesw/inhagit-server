package inha.git.user.api.controller.dto.request;

import inha.git.common.validation.annotation.ValidParameter;
import inha.git.common.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record UpdatePwRequest(

        @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
        @ValidPassword
        @ValidParameter
        @Schema(description = "비밀번호", example = "password2@")
        String pw
) {
}
