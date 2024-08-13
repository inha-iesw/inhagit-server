package inha.git.user.api.controller;

import inha.git.common.BaseResponse;
import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.response.CompanySignupResponse;
import inha.git.user.api.controller.dto.response.ProfessorSignupResponse;
import inha.git.user.api.controller.dto.response.StudentSignupResponse;
import inha.git.user.api.service.CompanyService;
import inha.git.user.api.service.ProfessorService;
import inha.git.user.api.service.StudentService;
import inha.git.user.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.SuccessStatus.*;

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
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CompanyService companyService;

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
        return BaseResponse.of(STUDENT_SIGN_UP_OK, studentService.studentSignup(studentSignupRequest));
    }

    /**
     * 교수 회원가입 API
     *
     * <p>교수 회원가입을 처리.</p>
     *
     * @param professorSignupRequest 교수 회원가입 요청 정보
     *
     * @return 교수 회원가입 결과를 포함하는 BaseResponse<ProfessorSignupResponse>
     */
    @PostMapping("/professor")
    @Operation(summary = "교수 회원가입 API", description = "교수 회원가입을 처리합니다.")
    public BaseResponse<ProfessorSignupResponse> professorSignup(@Validated @RequestBody ProfessorSignupRequest professorSignupRequest) {
        return BaseResponse.of(PROFESSOR_SIGN_UP_OK, professorService.professorSignup(professorSignupRequest));
    }

    /**
     * 기업 회원가입 API
     *
     * <p>기업 회원가입을 처리.</p>
     *
     * @param companySignupRequest 기업 회원가입 요청 정보
     *
     * @return 기업 회원가입 결과를 포함하는 BaseResponse<CompanySignupResponse>
     */
    @PostMapping(value = "/company",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기업 회원가입 API", description = "기업 회원가입을 처리합니다.")
    public BaseResponse<CompanySignupResponse> companySignup(
            @Validated @RequestPart("company") CompanySignupRequest companySignupRequest,
            @RequestPart(value = "evidence" ) MultipartFile evidence) {
        return BaseResponse.of(COMPANY_SIGN_UP_OK, companyService.companySignup(companySignupRequest, evidence));
    }

}
