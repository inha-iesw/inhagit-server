package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.admin.api.service.AdminSearchService;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
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
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
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
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
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
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
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
            @PathVariable("userIdx") Integer userIdx) {
        return BaseResponse.of(USER_DETAIL_OK, adminSearchService.getAdminUser(userIdx));
    }
}
