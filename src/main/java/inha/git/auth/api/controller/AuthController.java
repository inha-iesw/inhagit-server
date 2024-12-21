package inha.git.auth.api.controller;

import inha.git.auth.api.controller.dto.request.*;
import inha.git.auth.api.controller.dto.response.FindEmailResponse;
import inha.git.auth.api.controller.dto.response.LoginResponse;
import inha.git.auth.api.service.AuthService;
import inha.git.auth.api.service.MailService;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.user.api.controller.dto.response.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.*;


/**
 * 인증 관련 API를 처리하는 컨트롤러입니다.
 * 로그인, 이메일 인증, 비밀번호 찾기 등 인증이 필요 없는 엔드포인트를 제공합니다.
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
     * 회원가입 및 인증을 위한 이메일 인증번호를 발송합니다.
     *
     * @param emailRequest 이메일 주소와 인증 타입(회원가입/비밀번호 찾기 등)을 포함한 요청
     * @return 이메일 발송 결과 메시지
     * @throws BaseException EMAIL_SEND_FAIL: 이메일 발송 실패 시
     */
    @PostMapping ("/number")
    @Operation(summary = "이메일 인증 API",description = "이메일 인증을 처리합니다.")
    public BaseResponse<String> mailSend(@RequestBody @Valid EmailRequest emailRequest){
        log.info("이메일 인증 이메일 : {}", emailRequest.email());
        return BaseResponse.of(EMAIL_SEND_OK, mailService.mailSend(emailRequest));
    }

    /**
     * 발송된 이메일 인증번호의 유효성을 검증합니다.
     *
     * @param emailCheckRequest 이메일 주소, 인증번호, 인증 타입을 포함한 요청
     * @return 인증 성공 여부 (true/false)
     * @throws BaseException EMAIL_AUTH_EXPIRED: 인증 시간 만료,
     *                      EMAIL_AUTH_NOT_MATCH: 인증번호 불일치
     */
    @PostMapping("/number/check")
    @Operation(summary = "이메일 인증 확인 API",description = "입력받은 인증번호의 유효성을 검증합니다.")
    public BaseResponse<Boolean> mailSendCheck(@RequestBody @Valid EmailCheckRequest emailCheckRequest) {
        log.info("이메일 인증 확인 이메일 : {}", emailCheckRequest.email());
        return BaseResponse.of(EMAIL_AUTH_OK, mailService.mailSendCheck(emailCheckRequest));
    }

    /**
     * 사용자 로그인을 처리합니다.
     * 로그인 성공 시 JWT 토큰을 발급합니다.
     *
     * @param loginRequest 이메일과 비밀번호를 포함한 로그인 요청
     * @return JWT 토큰과 사용자 정보를 포함한 응답
     * @throws BaseException ACCOUNT_LOCKED: 계정 잠김,
     *                      EMAIL_NOT_FOUND: 존재하지 않는 이메일
     *                      BLOCKED_USER: 차단된 사용자
     *                      NOT_APPROVED_USER: 승인되지 않은 사용자
     */
    @PostMapping("/login")
    @Operation(summary = "로그인 API",description = "이메일/비밀번호로 로그인을 처리합니다.")
    public BaseResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 시도 이메일 : {}", loginRequest.email());
        return BaseResponse.of(LOGIN_OK, authService.login(loginRequest));
    }

    /**
     * 학번과 이름으로 사용자의 이메일(아이디)을 찾습니다.
     *
     * @param findEmailRequest 학번과 이름을 포함한 요청
     * @return 찾은 이메일 정보
     * @throws BaseException USER_NOT_FOUND: 일치하는 사용자 정보가 없는 경우
     */
    @PostMapping("/find/email")
    @Operation(summary = "아이디 찾기 API",description = "학번과 이름으로 이메일을 찾습니다.")
    public BaseResponse<FindEmailResponse> findEmail(@RequestBody @Valid FindEmailRequest findEmailRequest) {
        log.info("아이디 찾기 시도 학번 : {} 이름 : {}", findEmailRequest.userNumber(), findEmailRequest.name());
        return BaseResponse.of(FIND_EMAIL_OK, authService.findEmail(findEmailRequest));
    }


    /**
     * 비밀번호 찾기를 위한 이메일 인증번호를 발송합니다.
     * 가입된 이메일인 경우에만 인증번호가 발송됩니다.
     *
     * @param findPasswordRequest 이메일 주소를 포함한 요청
     * @return 이메일 발송 결과 메시지
     * @throws BaseException EMAIL_NOT_FOUND: 가입되지 않은 이메일인 경우,
     *                      EMAIL_SEND_FAIL: 이메일 발송 실패
     */
    @PostMapping ("/find/pw")
    @Operation(summary = "비밀번호 찾기 이메일 인증 API",description = "비밀번호 찾기 이메일 인증을 처리합니다.")
    public BaseResponse<String> findPasswordMailSend(@RequestBody @Valid FindPasswordRequest findPasswordRequest){
        log.info("비밀번호 찾기 이메일 인증 이메일 : {}", findPasswordRequest.email());
        return BaseResponse.of(FIND_PASSWORD_EMAIL_OK, mailService.findPasswordMailSend(findPasswordRequest));
    }

    /**
     * 비밀번호 찾기를 위해 발송된 인증번호를 검증합니다.
     *
     * @param fdindPasswordCheckRequest 이메일과 인증번호를 포함한 요청
     * @return 인증 성공 여부 (true/false)
     * @throws BaseException EMAIL_AUTH_EXPIRED: 인증 시간 만료,
     *                      EMAIL_AUTH_NOT_MATCH: 인증번호 불일치
     */
    @PostMapping ("/find/pw/check")
    @Operation(summary = "비밀번호 찾기 이메일 인증 확인 API",description = "비밀번호 찾기 이메일 인증 확인을 처리합니다.")
    public BaseResponse<Boolean> findPasswordMailSendCheck(@RequestBody @Valid FindPasswordCheckRequest fdindPasswordCheckRequest){
        log.info("비밀번호 찾기 이메일 인증 확인 이메일 : {}", fdindPasswordCheckRequest.email());
        return BaseResponse.of(FIND_PASSWORD_EMAIL_AUTH_OK, mailService.findPasswordMailSendCheck(fdindPasswordCheckRequest));
    }

    /**
     * 이메일 인증 후 새로운 비밀번호로 변경합니다.
     * 이메일 인증이 완료된 경우에만 비밀번호 변경이 가능합니다.
     *
     * @param changePasswordRequest 이메일과 새로운 비밀번호를 포함한 요청
     * @return 비밀번호가 변경된 사용자 정보
     * @throws BaseException EMAIL_AUTH_NOT_FOUND: 이메일 인증이 완료되지 않은 경우,
     *                      EMAIL_NOT_FOUND: 존재하지 않는 이메일
     */
    @PostMapping("/find/pw/change")
    @Operation(summary = "비밀번호 변경 API",description = "비밀번호 변경을 처리합니다.")
    public BaseResponse<UserResponse> findPassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        log.info("비밀번호 변경 이메일 : {}", changePasswordRequest.email());
        return BaseResponse.of(CHANGE_PASSWORD_OK, authService.changePassword(changePasswordRequest));
    }
}
