package inha.git.user.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.team.api.controller.dto.response.SearchMyTeamsResponse;
import inha.git.user.api.controller.dto.request.CompanySignupRequest;
import inha.git.user.api.controller.dto.request.ProfessorSignupRequest;
import inha.git.user.api.controller.dto.request.StudentSignupRequest;
import inha.git.user.api.controller.dto.request.UpdatePwRequest;
import inha.git.user.api.controller.dto.response.*;
import inha.git.user.api.service.CompanyService;
import inha.git.user.api.service.ProfessorService;
import inha.git.user.api.service.StudentService;
import inha.git.user.api.service.UserService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.*;

/**
 * UserController는 유저 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "user controller", description = "유저 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final StudentService studentService;
    private final ProfessorService professorService;
    private final CompanyService companyService;


    /**
     * 특정 유저 조회 API
     *
     * <p>특정 유저를 조회.</p>
     *
     * @return 특정 유저 조회 결과를 포함하는 BaseResponse<SearchUserResponse>
     */
    @GetMapping
    @Operation(summary = "특정 유저 조회 API", description = "특정 유저를 조회합니다.")
    public BaseResponse<SearchUserResponse> getLoginUser(@AuthenticationPrincipal User user) {
        return BaseResponse.of(MY_PAGE_USER_SEARCH_OK, userService.getUser(user.getId()));
    }

    /**
     * 특정 유저 조회 API
     *
     * <p>특정 유저를 조회.</p>
     *
     * @PathVariable userIdx 조회할 유저의 idx
     *
     * @return 특정 유저 조회 결과를 포함하는 BaseResponse<SearchUserResponse>
     */
    @GetMapping("/{userIdx}")
    @Operation(summary = "특정 유저 조회 API", description = "특정 유저를 조회합니다.")
    public BaseResponse<SearchUserResponse> getUser(@PathVariable("userIdx" ) Integer userIdx) {
        return BaseResponse.of(MY_PAGE_USER_SEARCH_OK, userService.getUser(userIdx));
    }

    /**
     * 특정 유저의 프로젝트 조회 API
     *
     * <p>특정 유저의 프로젝트를 조회.</p>
     *
     * @param user 인증된 유저 정보
     * @param page 페이지 번호
     *
     * @return 특정 유저의 프로젝트 조회 결과를 포함하는 BaseResponse<Page<SearchProjectsResponse>>
     */
    @GetMapping("/{userIdx}/projects")
    @Operation(summary = "특정 유저의 프로젝트 조회 API", description = "특정 유저의 프로젝트를 조회합니다.")
    public BaseResponse<Page<SearchProjectsResponse>> getUserProjects(@AuthenticationPrincipal User user,
                                                                      @PathVariable("userIdx") Integer userIdx,
                                                                      @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(MY_PAGE_PROJECT_SEARCH_OK, userService.getUserProjects(user, userIdx, page - 1));
    }

    /**
     * 특정 유저의 질문 조회 API
     *
     * <p>특정 유저의 질문을 조회.</p>
     *
     * @param user 인증된 유저 정보
     * @param page 페이지 번호
     *
     * @return 특정 유저의 질문 조회 결과를 포함하는 BaseResponse<Page<SearchQuestionsResponse>>
     */
    @GetMapping("/{userIdx}/questions")
    @Operation(summary = "특정 유저의 질문 조회 API", description = "특정 유저의 질문을 조회합니다.")
    public BaseResponse<Page<SearchQuestionsResponse>> getUserQuestions(@AuthenticationPrincipal User user,
                                                                          @PathVariable("userIdx") Integer userIdx,
                                                                        @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(MY_PAGE_QUESTION_SEARCH_OK, userService.getUserQuestions(user,userIdx, page - 1));
    }

    /**
     * 특정 유저의 팀 조회 API
     *
     * <p>특정 유저의 팀을 조회.</p>
     *
     * @param user 인증된 유저 정보
     * @param page 페이지 번호
     *
     * @return 특정 유저의 팀 조회 결과를 포함하는 BaseResponse<Page<SearchMyTeamsResponse>>
     */
    @GetMapping("/{userIdx}/teams")
    @Operation(summary = "특정 유저의 팀 조회 API", description = "특정 유저의 팀을 조회합니다.")
    public BaseResponse<Page<SearchMyTeamsResponse>> getUserTeams(@AuthenticationPrincipal User user,
                                                                  @PathVariable("userIdx") Integer userIdx,
                                                                  @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(MY_PAGE_TEAM_SEARCH_OK, userService.getUserTeams(user, userIdx, page - 1));
    }


    /**
     * 특정 유저의 참여중인 문제 조회 API
     *
     * <p>특정 유저의 참여중인 문제를 조회.</p>
     *
     * @param user 인증된 유저 정보
     * @param page 페이지 번호
     *
     * @return 특정 유저의 참여중인 문제 조회 결과를 포함하는 BaseResponse<Page<SearchProblemsResponse>>
     */
    @GetMapping("/{userIdx}/problems")
    @Operation(summary = "특정 유저의 참여중인 문제 조회 API", description = "특정 유저의 참여중인 문제를 조회합니다.")
    public BaseResponse<Page<SearchProblemsResponse>> getUserProblems(@AuthenticationPrincipal User user,
                                                                      @PathVariable("userIdx") Integer userIdx,
                                                                      @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(MY_PAGE_PROBLEM_SEARCH_OK, userService.getUserProblems(user, userIdx,page - 1));
    }
    /**
     * 학생 회원가입 API
     *
     * <p>학생 회원가입을 처리.</p>
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
            @Validated @NotNull(message = "evidence 파일을 첨부해주세요.")
            @RequestPart(value = "evidence" ) MultipartFile evidence) {
        return BaseResponse.of(COMPANY_SIGN_UP_OK, companyService.companySignup(companySignupRequest, evidence));
    }

    @PutMapping("/pw")
    @Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경합니다.")
    public BaseResponse<UserResponse> changePassword(@AuthenticationPrincipal User user, @Validated @RequestBody UpdatePwRequest updatePwRequest) {
        return BaseResponse.of(PW_CHANGE_OK, userService.changePassword(user.getId(), updatePwRequest));
    }
}
