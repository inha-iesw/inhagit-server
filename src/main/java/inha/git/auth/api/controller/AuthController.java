package inha.git.auth.api.controller;

import inha.git.auth.api.controller.dto.request.*;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
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
    public BaseResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 시도 이메일 : {}", loginRequest.email());
        return BaseResponse.of(LOGIN_OK, authService.login(loginRequest));
    }

    /**
     * 아이디 찾기 API
     *
     * <p>아이디 찾기를 처리.</p>
     *
     * @param findEmailRequest 아이디 찾기 요청 정보
     *
     * @return 아이디 찾기 결과를 포함하는 BaseResponse<FindEmailResponse>
     */
    @PostMapping("/find/email")
    @Operation(summary = "아이디 찾기 API",description = "아이디 찾기를 처리합니다.")
    public BaseResponse<FindEmailResponse> findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        log.info("아이디 찾기 시도 학번 : {} 이름 : {}", findEmailRequest.userNumber(), findEmailRequest.name());
        return BaseResponse.of(FIND_EMAIL_OK, authService.findEmail(findEmailRequest));
    }


    @PostMapping ("/find/password")
    @Operation(summary = "비밀번호 찾기 이메일 인증 API",description = "비밀번호 찾기 이메일 인증을 처리합니다.")
    public BaseResponse<String> findPasswordMailSend(@RequestBody @Valid FindPasswordRequest findPasswordRequest){
        log.info("비밀번호 찾기 이메일 인증 이메일 : {}", findPasswordRequest.email());
        return BaseResponse.of(FIND_PASSWORD_EMAIL_OK, mailService.findPasswordMailSend(findPasswordRequest));
    }

//    @PostMapping ("/find/password/check")
//    @Operation(summary = "이메일 인증 API",description = "이메일 인증을 처리합니다.")
//    public BaseResponse<String> findPasswordMailSendCheck(@RequestBody @Valid EmailRequest emailRequest){
//        log.info("이메일 인증 이메일 : {}", emailRequest.email());
//        return BaseResponse.of(EMAIL_SEND_OK, mailService.findPasswordMailSendCheck(emailRequest));
//    }

//    @PostMapping("/find/password/change")
//    public BaseResponse<String> findPassword(@RequestBody @Valid FindEmailRequest findEmailRequest) {
//        log.info("비밀번호 찾기 시도 학번 : {} 이름 : {}", findEmailRequest.userNumber(), findEmailRequest.name());
//        return BaseResponse.of(FIND_PASSWORD_OK, authService.findPassword(findEmailRequest));
//    }





}
