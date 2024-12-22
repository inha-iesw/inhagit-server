package inha.git.department.api.controller;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;
import inha.git.department.api.service.DepartmentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * 학과 관련 API를 처리하는 컨트롤러입니다.
 * 학과의 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@Slf4j
@Tag(name = "department controller", description = "department 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 학과 목록을 조회합니다.
     * 단과대학 ID가 제공되면 해당 단과대학의 학과만 조회하고,
     * 제공되지 않으면 모든 학과를 조회합니다.
     *
     * @param collegeIdx 조회할 단과대학 ID (선택적)
     * @return 학과 목록을 포함한 응답
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @GetMapping
    @Operation(summary = "학과 전체 조회 API", description = "학과 전체를 조회합니다.")
    public BaseResponse<List<SearchDepartmentResponse>> getDepartments(@RequestParam(value = "collegeIdx", required = false) Integer collegeIdx) {
        return BaseResponse.of(DEPARTMENT_SEARCH_OK, departmentService.getDepartments(collegeIdx));
    }

    /**
     * 새로운 학과를 생성합니다.
     * 관리자 권한을 가진 사용자만 접근 가능합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param createDepartmentRequest 생성할 학과 정보 (학과명, 단과대학 ID)
     * @return 학과 생성 결과 메시지
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우,
     *                      DEPARTMENT_NOT_BELONG_TO_COLLEGE: 학과와 단과대학 정보가 일치하지 않는 경우
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "학과 생성(관리자 전용) API", description = "학과를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createDepartment(@AuthenticationPrincipal User user,
                                                 @Validated @RequestBody CreateDepartmentRequest createDepartmentRequest) {
        log.info("학과 생성 - 관리자: {} 학과명: {}", user.getName(), createDepartmentRequest.name());
        return BaseResponse.of(DEPARTMENT_CREATE_OK, departmentService.createDepartment(user, createDepartmentRequest));
    }

    /**
     * 학과명을 수정합니다.
     * 관리자 권한을 가진 사용자만 접근 가능합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param departmentIdx 수정할 학과의 식별자
     * @param updateDepartmentRequest 새로운 학과명
     * @return 학과명 수정 결과 메시지
     * @throws BaseException DEPARTMENT_NOT_FOUND: 학과를 찾을 수 없는 경우
     */
    @PutMapping("/{departmentIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "학과명 수정(관리자 전용) API", description = "학과명을 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateDepartmentName(@AuthenticationPrincipal User user,
                                                     @PathVariable("departmentIdx") Integer departmentIdx,
                                                     @Validated @RequestBody UpdateDepartmentRequest updateDepartmentRequest) {
        log.info("학과명 수정 - 관리자: {} 학과명: {}", user.getName(), updateDepartmentRequest.name());
        return BaseResponse.of(DEPARTMENT_UPDATE_OK, departmentService.updateDepartmentName(user, departmentIdx, updateDepartmentRequest));
    }

    /**
     * 학과를 삭제(비활성화) 처리합니다.
     * 관리자 권한을 가진 사용자만 접근 가능합니다.
     * 실제 삭제가 아닌 소프트 삭제로 처리됩니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param departmentIdx 삭제할 학과의 식별자
     * @return 학과 삭제 결과 메시지
     * @throws BaseException DEPARTMENT_NOT_FOUND: 학과를 찾을 수 없는 경우
     */
    @DeleteMapping("/{departmentIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "학과 삭제(관리자 전용) API", description = "학과를 soft 삭제합니다.(관리자 전용)")
    public BaseResponse<String> deleteDepartment(@AuthenticationPrincipal User user,
                                                 @PathVariable("departmentIdx") Integer departmentIdx) {
        log.info("학과 삭제 - 관리자: {} 학과 인덱스: {}", user.getName(), departmentIdx);
        return BaseResponse.of(DEPARTMENT_DELETE_OK, departmentService.deleteDepartment(user, departmentIdx));
    }
}
