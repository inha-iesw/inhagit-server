package inha.git.auth.api.controller;

import inha.git.auth.api.controller.dto.request.SignupRequest;
import inha.git.auth.api.controller.dto.response.SignupResponse;
import inha.git.auth.api.service.AuthService;
import inha.git.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.SIGN_UP_OK;


/**
 * AuthController는 인증 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "auth controller", description = "인증 필요 없는 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 서버 상태 확인 API
     *
     * <p>서버의 상태를 확인.</p>
     *
     * @return 서버 상태 메시지를 포함하는 BaseResponse<String>
     */
    @GetMapping("/health")
    @Operation(summary = "서버 상태 확인 API",description = "서버 상태를 확인합니다.")
    public BaseResponse<String> health() {
        return BaseResponse.onSuccess("I'm healthy");
    }

    /**
     * 회원 가입 API
     *
     * <p>회원 가입을 처리.</p>
     *
     * @param signupRequest 회원 가입 요청 정보
     *
     * @return 회원 가입 결과를 포함하는 BaseResponse<SignupResponse>
     */
    @PostMapping("/signup")
    @Operation(summary = "회원 가입 API",description = "회원 가입을 처리합니다.")
    public BaseResponse<SignupResponse> signup(@Validated @RequestBody SignupRequest signupRequest) {
        return BaseResponse.of(SIGN_UP_OK, authService.signup(signupRequest));
    }

    /**
     * 로그인 API
     *
     * <p>로그인을 처리.</p>
     *
     * @param loginRequest 로그인 요청 정보
     *
     * @return 로그인 결과를 포함하는 BaseResponse<LoginResponse>
     */
    @PostMapping("/login")
    @Operation(summary = "로그인 API",description = "로그인을 처리합니다.")
    public BaseResponse<LoginResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        return BaseResponse.of(LOGIN_OK, authService.login(loginRequest));
    }

    /**
     * 리프레시 토큰 재발급 API
     *
     * <p>리프레시 토큰을 이용해 새로운 엑세스 토큰을 발급.</p>
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     *
     * @return 새로운 엑세스 토큰을 포함하는 BaseResponse<RefreshResponse>
     */
    @PostMapping("/refresh-token")
    @Operation(summary = "리프레시토큰 재발급 API",description = "엑세스 토큰 만료 시 리프레시 토큰을 이용해 새로운 엑세스 토큰을 발급합니다.")
    public BaseResponse<RefreshResponse> refreshToken(HttpServletRequest request, HttpServletResponse response)  {
        return BaseResponse.of(REFRESH_OK, authService.refreshToken(request, response));
    }

}
