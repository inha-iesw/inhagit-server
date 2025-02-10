package inha.git.user.api.controller.dto.request;

import inha.git.common.validation.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * SignupRequest는 회원 가입 요청 정보를 담는 DTO 클래스.
 */
public record CompanySignupRequest(

        @NotEmpty(message = "이메일은 필수 입력 항목입니다.")
        @EmailUnique
        @Email
        @ValidEmail
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@gamil.com")
        String email,

        @NotEmpty(message = "이름은 필수 입력 항목입니다.")
        @ValidName
        @ValidParameter
        @Schema(description = "이름", example = "홍길동")
        String name,

        @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
        @ValidPassword
        @ValidParameter
        @Schema(description = "비밀번호", example = "password2@")
        String pw,

        @NotBlank(message = "소속을 입력하세요.")
        @Size(min = 1, max = 12, message = "소속은 1자에서 12자 사이여야 합니다.")
        @Schema(description = "소속", example = "인하대학교")
        String affiliation
) {
}
