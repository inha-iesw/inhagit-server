package inha.git.user.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * SignupResponse는 회원 가입 응답 정보를 담는 DTO 클래스.
 */
public record StudentSignupResponse(

        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Long userId){ }