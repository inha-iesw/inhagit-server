package inha.git.user.api.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * SignupResponse는 회원 가입 응답 정보를 담는 DTO 클래스.
 */
public record StudentSignupResponse(

        @NotNull
        @Schema(description = "유저 아이디", example = "1")
        Long userId,
        @NotNull
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTYzNzIwNjIwM30.1J9")
        String accessToken,
        @NotNull
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImV4cCI6MTYzNzIwNjIwM30.1J9")
        String refreshToken,
        @NotNull
        @Schema(description = "닉네임", example = "테스트유저")
        String nickname) {
}
