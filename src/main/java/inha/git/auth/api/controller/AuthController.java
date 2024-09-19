package inha.git.auth.api.controller;

import inha.git.auth.api.controller.dto.request.EmailCheckRequest;
import inha.git.auth.api.controller.dto.request.EmailRequest;
import inha.git.auth.api.controller.dto.request.LoginRequest;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.service.AuthService;
import inha.git.auth.api.service.MailService;
import inha.git.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.*;


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
    private final MailService mailService;


    /**
     * 이메일 인증 API
     *
     * <p>이메일 인증을 처리.</p>
     *
     * @param emailRequest 이메일 인증 요청 정보
     *
     * @return 이메일 인증 결과를 포함하는 BaseResponse<String>
     */
    @PostMapping ("/number")
    @Operation(summary = "이메일 인증 API",description = "이메일 인증을 처리합니다.")
    public BaseResponse<String> mailSend(@RequestBody @Valid EmailRequest emailRequest){
        log.info("이메일 인증 이메일 : {}", emailRequest.email());
        return BaseResponse.of(EMAIL_SEND_OK, mailService.mailSend(emailRequest));
    }

    /**
     * 이메일 인증 확인 API
     *
     * <p>이메일 인증 확인을 처리.</p>
     *
     * @param emailCheckRequest 이메일 인증 확인 요청 정보
     *
     * @return 이메일 인증 확인 결과를 포함하는 BaseResponse<Boolean>
     */
    @PostMapping("/number/check")
    @Operation(summary = "이메일 인증 확인 API",description = "이메일 인증 확인을 처리합니다.")
    public BaseResponse<Boolean> mailSendCheck(@RequestBody @Valid EmailCheckRequest emailCheckRequest) {
        log.info("이메일 인증 확인 이메일 : {}", emailCheckRequest.email());
        return BaseResponse.of(EMAIL_AUTH_OK, mailService.mailSendCheck(emailCheckRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API",description = "로그인을 처리합니다.")
    public BaseResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 시도 이메일 : {}", loginRequest.email());
        return BaseResponse.of(LOGIN_OK, authService.login(loginRequest));
    }







}
