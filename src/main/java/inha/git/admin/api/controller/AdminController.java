package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.admin.api.service.AdminService;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.*;

/**
 * AdminController는 관리자 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "admin controller", description = "admin 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminService adminService;

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
    @PreAuthorize("hasAuthority('admin:read')")
    @Operation(summary = "관리자 전용 유저 검색 API", description = "관리자 전용 유저 검색 API입니다")
    public BaseResponse<Page<SearchUserResponse>> getAdminUsers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 0) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(USER_SEARCH_OK, adminService.getAdminUsers(search, page));
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
    @PreAuthorize("hasAuthority('admin:read')")
    @Operation(summary = "관리자 전용 학생 검색 API", description = "관리자 전용 학생 검색 API입니다")
    public BaseResponse<Page<SearchStudentResponse>> getAdminStudents(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 0) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(STUDENT_SEARCH_OK, adminService.getAdminStudents(search, page));
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
    @PreAuthorize("hasAuthority('admin:read')")
    @Operation(summary = "관리자 전용 교수 검색 API", description = "관리자 전용 교수 검색 API입니다")
    public BaseResponse<Page<SearchProfessorResponse>> getAdminProfessors(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 0) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(PROFESSOR_SEARCH_OK, adminService.getAdminProfessors(search, page));
    }

    /**
     * 관리자 전용 회사 검색 API
     *
     * <p>관리자 전용 회사 검색 API입니다.</p>
     *
     * @param search 검색어
     * @param page 페이지 번호
     * @return 검색된 회사 정보를 포함하는 BaseResponse<Page<SearchCompanyResponse>>
     */
    @GetMapping("/companies")
    @PreAuthorize("hasAuthority('admin:read')")
    @Operation(summary = "관리자 전용 회사 검색 API", description = "관리자 전용 회사 검색 API입니다")
    public BaseResponse<Page<SearchCompanyResponse>> getAdminCompanies(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam("page") Integer page) {
        if (page < 0) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(COMPANY_SEARCH_OK, adminService.getAdminCompanies(search, page));
    }
}
