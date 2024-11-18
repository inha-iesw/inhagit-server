package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.request.SearchReportCond;
import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.admin.api.service.AdminSearchService;
import inha.git.bug_report.api.controller.dto.request.SearchBugReportCond;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportsResponse;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.*;

/**
 * AdminApproveController는 관리자 전용 계정 승인 관련 엔드포인트를 처리.
 */

@Slf4j
@Tag(name = "admin search controller", description = "admin search 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminSearchController {

    private final AdminSearchService adminSearchService;

    /**
     * 관리자 전용 유저 검색 API
     *
     * <p>관리자 전용 유저 검색 API입니다.</p>
     *
     * @param search 검색어
     * @param page 페이지 번호
     * @return 검색된 유저 정보를 포함하는 BaseResponse<Page<SearchUserResponse>>
     */
    @GetMapping("/users")
    @Operation(summary = "유저 검색 API(관리자 전용)", description = "관리자 전용 유저 검색 API입니다")
    public BaseResponse<Page<SearchUserResponse>> getAdminUsers(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 관리자 유저 검색 - 검색어: {}, 페이지: {}", user.getName(), search, page);
        return BaseResponse.of(USER_SEARCH_OK, adminSearchService.getAdminUsers(search, page - 1));
    }
    /**
     * 관리자 전용 학생 검색 API
     *
     * <p>관리자 전용 학생 검색 API입니다.</p>
     *
     * @param search 검색어
     * @param page 페이지 번호
     * @return 검색된 학생 정보를 포함하는 BaseResponse<Page<SearchStudentResponse>>
     */
    @GetMapping("/students")
    @Operation(summary = "학생 검색 API(관리자 전용)", description = "관리자 전용 학생 검색 API입니다")
    public BaseResponse<Page<SearchStudentResponse>> getAdminStudents(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 관리자 학생 검색 - 검색어: {}, 페이지: {}", user.getName(), search, page);
        return BaseResponse.of(STUDENT_SEARCH_OK, adminSearchService.getAdminStudents(search, page - 1));
    }
    /**
     * 관리자 전용 교수 검색 API
     *
     * <p>관리자 전용 교수 검색 API입니다.</p>
     *
     * @param search 검색어
     * @param page 페이지 번호
     * @return 검색된 교수 정보를 포함하는 BaseResponse<Page<SearchProfessorResponse>>
     */
    @GetMapping("/professors")
    @Operation(summary = "교수 검색 API(관리자 전용)", description = "관리자 전용 교수 검색 API입니다")
    public BaseResponse<Page<SearchProfessorResponse>> getAdminProfessors(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 관리자 교수 검색 - 검색어: {}, 페이지: {}", user.getName(), search, page);
        return BaseResponse.of(PROFESSOR_SEARCH_OK, adminSearchService.getAdminProfessors(search, page - 1));
    }

    /**
     * 관리자 전용 기업 검색 API
     *
     * <p>관리자 전용 기업 검색 API입니다.</p>
     *
     * @param search 검색어
     * @param page 페이지 번호
     * @return 검색된 회사 정보를 포함하는 BaseResponse<Page<SearchCompanyResponse>>
     */
    @GetMapping("/companies")
    @Operation(summary = "기업 검색 API(관리자 전용)", description = "관리자 전용 회사 검색 API입니다")
    public BaseResponse<Page<SearchCompanyResponse>> getAdminCompanies(
            @AuthenticationPrincipal User user,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 관리자 회사 검색 - 검색어: {}, 페이지: {}", user.getName(), search, page);
        return BaseResponse.of(COMPANY_SEARCH_OK, adminSearchService.getAdminCompanies(search, page - 1));
    }

    /**
     * 특정 유저 조회 API
     *
     * <p>특정 유저를 조회.</p>
     *
     * @param userIdx 유저 인덱스
     *
     * @return 특정 유저 조회 결과를 포함하는 BaseResponse<SearchUserResponse>
     */
    @GetMapping("/users/{userIdx}")
    @Operation(summary = "특정 유저 조회(관리자 전용) API", description = "특정 유저를 조회합니다.")
    public BaseResponse<inha.git.user.api.controller.dto.response.SearchUserResponse> getAdminUser(
            @AuthenticationPrincipal User user,
            @PathVariable("userIdx") Integer userIdx) {
        log.info("{} 관리자 유저 조회 - 조회할 유저: {}", user.getName(), userIdx);
        return BaseResponse.of(USER_DETAIL_OK, adminSearchService.getAdminUser(userIdx));
    }

    /**
     * 신고 조회 API
     *
     * <p>신고 조회 API입니다.</p>
     *
     * @param searchReportCond 신고 검색 조건
     * @param page 페이지 번호
     * @return 검색된 신고 정보를 포함하는 BaseResponse<Page<SearchReportResponse>>
     */
    @GetMapping("/report")
    @Operation(summary = "신고 조회 API(관리자 전용)", description = "관리자 전용 신고 조회 API입니다")
    public BaseResponse<Page<SearchReportResponse>> getAdminReports(
            @AuthenticationPrincipal User user,
            @Validated @ModelAttribute SearchReportCond searchReportCond,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 관리자 신고 검색 - 페이지: {}", user.getName(), page);
        return BaseResponse.of(REPORT_SEARCH_OK, adminSearchService.getAdminReports(searchReportCond, page - 1));
    }

    /**
     * 버그 제보 조회 API
     *
     * <p>버그 제보 조회 API입니다.</p>
     *
     * @param searchBugReportCond 버그 제보 검색 조건
     * @param page 페이지 번호
     * @return 검색된 버그 제보 정보를 포함하는 BaseResponse<Page<SearchBugReportsResponse>>
     */
    @GetMapping("/bug-report")
    @Operation(summary = "버그 제보 조회 API(관리자 전용)", description = "관리자 전용 버그 제보 조회 API입니다")
    public BaseResponse<Page<SearchBugReportsResponse>> getAdminBugReports(
            @AuthenticationPrincipal User user,
            @Validated @ModelAttribute SearchBugReportCond searchBugReportCond,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        log.info("{} 관리자 버그 제보 조회 - 페이지: {}", user.getName(), page);
        return BaseResponse.of(BUG_REPORT_SEARCH_OK, adminSearchService.getAdminBugReports(searchBugReportCond, page - 1));
    }
}
