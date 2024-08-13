package inha.git.user.api.controller;

import inha.git.common.BaseResponse;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.STUDENT_SIGN_UP_OK;

/**
 * UserController는 유저 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "user controller", description = "유저 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    /**
     * 학생 회원가입 API
     *
     * <p>학생 회원가입을 처리ㄷ.</p>
     *
     * @param studentSignupRequest 학생 회원가입 요청 정보
     *
     * @return 학생 회원가입 결과를 포함하는 BaseResponse<StudentSignupResponse>
     */
    @PostMapping("/student")
    @Operation(summary = "학생 회원가입 API", description = "학생 회원가입을 처리합니다.")
    public BaseResponse<StudentSignupResponse> studentSignup(@Validated @RequestBody StudentSignupRequest studentSignupRequest) {
        return BaseResponse.of(STUDENT_SIGN_UP_OK, userService.studentSignup(studentSignupRequest));
    }

}
