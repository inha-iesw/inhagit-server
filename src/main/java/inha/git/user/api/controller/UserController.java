package inha.git.user.api.controller;

import inha.git.bug_report.api.controller.dto.request.SearchBugReportCond;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportsResponse;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
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
import inha.git.utils.PagingUtils;
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

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * 사용자 관련 API를 처리하는 컨트롤러입니다.
 * 일반 사용자, 학생, 교수, 기업회원의 회원가입과 정보 관리 기능을 제공합니다.
 * 사용자 조회, 프로젝트/팀/문제 참여 현황 등의 조회 기능을 포함합니다.
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
     * 현재 로그인한 사용자의 상세 정보를 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @return BaseResponse<SearchUserResponse> 사용자의 기본 정보와 통계 정보를 포함한 응답
     */
    @GetMapping
    @Operation(summary = "로그인 유저 조회 API", description = "현재 로그인한 유저의 정보를 조회합니다.")
    public BaseResponse<SearchUserResponse> getLoginUser(@AuthenticationPrincipal User user) {
        return BaseResponse.of(MY_PAGE_USER_SEARCH_OK, userService.getUser(user.getId()));
    }

    /**
     * 특정 사용자의 상세 정보를 조회합니다.
     *
     * @param userIdx 조회할 대상 사용자의 식별자
     * @return BaseResponse<SearchUserResponse> 사용자의 기본 정보와 통계 정보를 포함한 응답
     * @throws BaseException 조회 대상 사용자가 존재하지 않는 경우
     */
    @GetMapping("/{userIdx}")
    @Operation(summary = "특정 유저 조회 API", description = "특정 유저를 조회합니다.")
    public BaseResponse<SearchUserResponse> getUser(@PathVariable("userIdx" ) Integer userIdx) {
        return BaseResponse.of(MY_PAGE_USER_SEARCH_OK, userService.getUser(userIdx));
    }

    /**
     * 특정 사용자가 참여중인 프로젝트 목록을 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param userIdx 조회할 대상 사용자의 식별자
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @return BaseResponse<Page<SearchProjectsResponse>> 프로젝트 목록을 포함한 페이징 응답
     * @throws BaseException 페이지 번호가 1 미만이거나, 조회 권한이 없는 경우
     */
    @GetMapping("/{userIdx}/projects")
    @Operation(summary = "특정 유저의 프로젝트 조회 API", description = "특정 유저의 프로젝트를 조회합니다.")
    public BaseResponse<Page<SearchProjectsResponse>> getUserProjects(@AuthenticationPrincipal User user,
                                                                      @PathVariable("userIdx") Integer userIdx,
                                                                      @RequestParam("page") Integer page) {
        PagingUtils.validatePage(page);
        return BaseResponse.of(MY_PAGE_PROJECT_SEARCH_OK, userService.getUserProjects(user, userIdx, PagingUtils.toPageIndex(page)));
    }

    /**
     * 특정 사용자가 작성한 질문 목록을 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param userIdx 조회할 대상 사용자의 식별자
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @return BaseResponse<Page<SearchQuestionsResponse>> 질문 목록을 포함한 페이징 응답
     * @throws BaseException 페이지 번호가 1 미만이거나, 조회 권한이 없는 경우
     */
    @GetMapping("/{userIdx}/questions")
    @Operation(summary = "특정 유저의 질문 조회 API", description = "특정 유저의 질문을 조회합니다.")
    public BaseResponse<Page<SearchQuestionsResponse>> getUserQuestions(@AuthenticationPrincipal User user,
                                                                          @PathVariable("userIdx") Integer userIdx,
                                                                        @RequestParam("page") Integer page) {
        PagingUtils.validatePage(page);
        return BaseResponse.of(MY_PAGE_QUESTION_SEARCH_OK, userService.getUserQuestions(user,userIdx, PagingUtils.toPageIndex(page)));
    }

    /**
     * 특정 사용자가 업로드한 문제 목록을 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param userIdx 조회할 대상 사용자의 식별자
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @return BaseResponse<Page<SearchProblemsResponse>> 문제 목록을 포함한 페이징 응답
     * @throws BaseException 페이지 번호가 1 미만이거나, 조회 권한이 없는 경우
     */
    @GetMapping("/{userIdx}/problems")
    @Operation(summary = "특정 유저가 업로드한 문제 조회 API", description = "특정가 업로드한 문제를 조회합니다.")
    public BaseResponse<Page<SearchProblemsResponse>> getUserProblems(@AuthenticationPrincipal User user,
                                                                      @PathVariable("userIdx") Integer userIdx,
                                                                      @RequestParam("page") Integer page) {
        PagingUtils.validatePage(page);
        return BaseResponse.of(MY_PAGE_PROBLEM_SEARCH_OK, userService.getUserProblems(user, userIdx, PagingUtils.toPageIndex(page)));
    }

    /**
     * 특정 사용자가 참여중인 문제 목록을 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param userIdx 조회할 대상 사용자의 식별자
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @return BaseResponse<Page<SearchProblemsResponse>> 문제 목록을 포함한 페이징 응답
     * @throws BaseException 페이지 번호가 1 미만이거나, 조회 권한이 없는 경우
     */
    @GetMapping("/{userIdx}/problems/participating")
    @Operation(summary = "특정 유저의 참여중인 문제 조회 API", description = "특정 유저의 참여중인 문제를 조회합니다.")
    public BaseResponse<Page<SearchProblemsResponse>> getUserProblemsParticipating(@AuthenticationPrincipal User user,
                                                                      @PathVariable("userIdx") Integer userIdx,
                                                                      @RequestParam("page") Integer page) {
        PagingUtils.validatePage(page);
        return BaseResponse.of(MY_PAGE_PROBLEM_PARTICIPATING_SEARCH_OK, userService.getUserProblemsParticipating(user, userIdx, PagingUtils.toPageIndex(page)));
    }

    /**
     * 특정 사용자가 작성한 신고 목록을 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param userIdx 조회할 대상 사용자의 식별자
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @return BaseResponse<Page<SearchReportResponse>> 신고 목록을 포함한 페이징 응답
     * @throws BaseException 페이지 번호가 1 미만이거나, 조회 권한이 없는 경우
     */
    @GetMapping("/{userIdx}/reports")
    @Operation(summary = "특정 유저의 신고 조회 API", description = "특정 유저의 신고를 조회합니다.")
    public BaseResponse <Page<SearchReportResponse>> getUserReports(@AuthenticationPrincipal User user,
                                                                    @PathVariable("userIdx") Integer userIdx,
                                                                    @RequestParam("page") Integer page) {
        PagingUtils.validatePage(page);
        return BaseResponse.of(MY_PAGE_REPORT_SEARCH_OK, userService.getUserReports(user, userIdx, PagingUtils.toPageIndex(page)));
    }

    /**
     * 특정 사용자가 작성한 버그 제보 목록을 조회합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param userIdx 조회할 대상 사용자의 식별자
     * @param searchBugReportCond 버그 제보 검색 조건
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @return BaseResponse<Page<SearchBugReportsResponse>> 버그 제보 목록을 포함한 페이징 응답
     * @throws BaseException 페이지 번호가 1 미만이거나, 조회 권한이 없는 경우
     */
    @GetMapping("/{userIdx}/bug-reports")
    @Operation(summary = "특정 유저의 버그 제보 조회 API", description = "특정 유저의 버그 제보를 조회합니다.")
    public BaseResponse <Page<SearchBugReportsResponse>> getUserBugReports(@AuthenticationPrincipal User user,
                                                                    @PathVariable("userIdx") Integer userIdx,
                                                                    @Validated @ModelAttribute SearchBugReportCond searchBugReportCond,
                                                                    @RequestParam("page") Integer page) {
        PagingUtils.validatePage(page);
        return BaseResponse.of(MY_PAGE_BUG_REPORT_SEARCH_OK, userService.getUserBugReports(user, userIdx, searchBugReportCond,  PagingUtils.toPageIndex(page)));
    }

    /**
     * 학생 회원가입을 처리합니다.
     *
     * @param studentSignupRequest 학생 회원가입 요청 정보 (이메일, 비밀번호, 이름, 학번, 학과 정보 등)
     * @return BaseResponse<StudentSignupResponse> 가입된 학생 정보를 포함한 응답
     * @throws BaseException 이메일 중복, 유효하지 않은 학과 정보, 이메일 인증 실패 등의 경우
     */
    @PostMapping("/student")
    @Operation(summary = "학생 회원가입 API", description = "학생 회원가입을 처리합니다.")
    public BaseResponse<StudentSignupResponse> studentSignup(@Validated @RequestBody StudentSignupRequest studentSignupRequest) {
        log.info("학생 회원가입 - 이메일: {} 이름: {} 학번: {}", studentSignupRequest.email(), studentSignupRequest.name(), studentSignupRequest.userNumber());
        return BaseResponse.of(STUDENT_SIGN_UP_OK, studentService.studentSignup(studentSignupRequest));
    }

    /**
     * 교수 회원가입을 처리합니다.
     *
     * @param professorSignupRequest 교수 회원가입 요청 정보 (이메일, 비밀번호, 이름, 사번, 학과 정보 등)
     * @return BaseResponse<ProfessorSignupResponse> 가입된 교수 정보를 포함한 응답
     * @throws BaseException 이메일 중복, 유효하지 않은 학과 정보, 이메일 인증 실패 등의 경우
     */
    @PostMapping("/professor")
    @Operation(summary = "교수 회원가입 API", description = "교수 회원가입을 처리합니다.")
    public BaseResponse<ProfessorSignupResponse> professorSignup(@Validated @RequestBody ProfessorSignupRequest professorSignupRequest) {
        log.info("교수 회원가입 - 이메일: {} 이름: {} 사번: {}", professorSignupRequest.email(), professorSignupRequest.name(), professorSignupRequest.userNumber());
        return BaseResponse.of(PROFESSOR_SIGN_UP_OK, professorService.professorSignup(professorSignupRequest));
    }

    /**
     * 기업 회원가입을 처리합니다.
     *
     * @param companySignupRequest 기업 회원가입 요청 정보 (이메일, 비밀번호, 이름, 회사명 등)
     * @param evidence 사업자등록증 파일
     * @return BaseResponse<CompanySignupResponse> 가입된 기업 정보를 포함한 응답
     * @throws BaseException 이메일 중복, 파일 업로드 실패, 이메일 인증 실패 등의 경우
     */
    @PostMapping(value = "/company",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기업 회원가입 API", description = "기업 회원가입을 처리합니다.")
    public BaseResponse<CompanySignupResponse> companySignup(
            @Validated @RequestPart("company") CompanySignupRequest companySignupRequest,
            @Validated @NotNull(message = "evidence 파일을 첨부해주세요.")
            @RequestPart(value = "evidence" ) MultipartFile evidence) {
        log.info("기업 회원가입 - 이메일: {} 이름: {} 소속: {}", companySignupRequest.email(), companySignupRequest.name(), companySignupRequest.affiliation());
        return BaseResponse.of(COMPANY_SIGN_UP_OK, companyService.companySignup(companySignupRequest, evidence));
    }

    /**
     * 로그인한 사용자의 비밀번호를 변경합니다.
     *
     * @param user 현재 인증된 사용자 정보
     * @param updatePwRequest 변경할 비밀번호 정보
     * @return BaseResponse<UserResponse> 비밀번호가 변경된 사용자 정보를 포함한 응답
     */
    @PutMapping("/pw")
    @Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경합니다.")
    public BaseResponse<UserResponse> changePassword(@AuthenticationPrincipal User user, @Validated @RequestBody UpdatePwRequest updatePwRequest) {
        log.info("비밀번호 변경 - 이메일: {}", user.getEmail());
        return BaseResponse.of(PW_CHANGE_OK, userService.changePassword(user.getId(), updatePwRequest));
    }
}
