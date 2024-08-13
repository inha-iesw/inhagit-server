package inha.git.user.api.controller.dto.request;

import inha.git.common.validation.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;


/**
 * SignupRequest는 회원 가입 요청 정보를 담는 DTO 클래스.
 */
public record StudentSignupRequest(

        @NotEmpty(message = "이메일은 필수 입력 항목입니다.")
        @EmailUnique
        @Email
        @ValidEmail
        @Schema(description = "유저 이메일", example = "ghkdrbgur13@inha.edu")
        String eamil,

        @NotEmpty(message = "이름은 필수 입력 항목입니다.")
        @ValidName
        @Schema(description = "이름", example = "홍길동")
        String name,

        @NotEmpty(message = "비밀번호는 필수 입력 항목입니다.")
        @ValidPassword
        @Schema(description = "비밀번호", example = "password2@")
        String pw,

        @NotEmpty(message = "학번은 필수 입력 항목입니다.")
        @ValidUserNumber
        @Schema(description = "학번", example = "12241234")
        String userNumber,

        @NotNull(message = "학과 목록은 필수 입력 항목입니다.")
        @Size(min = 1, message = "최소 한 개의 학과를 선택해야 합니다.")
        List<Integer> departmentIdxList
) {
}
