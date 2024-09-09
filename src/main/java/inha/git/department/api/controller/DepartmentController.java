package inha.git.department.api.controller;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.common.BaseResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.controller.dto.request.UpdateDepartmentRequest;
import inha.git.department.api.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * DepartmentController는 학과 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "department controller", description = "department 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 학과 전체 조회 API
     *
     * <p>학과 전체를 조회합니다.</p>
     *
     * @param collegeIdx 대학 인덱스
     *
     * @return 학과 전체 조회 결과를 포함하는 BaseResponse<List<SearchDepartmentResponse>>
     */
    @GetMapping
    @Operation(summary = "학과 전체 조회 API", description = "학과 전체를 조회합니다.")
    public BaseResponse<List<SearchDepartmentResponse>> getDepartments(@RequestParam(value = "collegeIdx", required = false) Integer collegeIdx) {
        return BaseResponse.of(DEPARTMENT_SEARCH_OK, departmentService.getDepartments(collegeIdx));
    }

    /**
     * 학과 생성 API
     *
     * <p>ADMIN계정만 호출 가능 -> 학과를 생성.</p>
     *
     * @param createDepartmentRequest 학과 생성 요청 정보
     *
     * @return 학과 생성 결과를 포함하는 BaseResponse<String>
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "학과 생성(관리자 전용) API", description = "학과를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createDepartment(@Validated @RequestBody CreateDepartmentRequest createDepartmentRequest) {
        return BaseResponse.of(DEPARTMENT_CREATE_OK, departmentService.createDepartment(createDepartmentRequest));
    }

    /**
     * 학과명 수정 API
     *
     * <p>ADMIN계정만 호출 가능 -> 학과명을 수정.</p>
     *
     * @param departmentIdx 학과 인덱스
     * @param updateDepartmentRequest 학과명 수정 요청 정보
     *
     * @return 학과명 수정 결과를 포함하는 BaseResponse<String>
     */
    @PutMapping("/{departmentIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "학과명 수정(관리자 전용) API", description = "학과명을 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateDepartmentName(@PathVariable("departmentIdx") Integer departmentIdx,
                                                     @Validated @RequestBody UpdateDepartmentRequest updateDepartmentRequest) {
        return BaseResponse.of(DEPARTMENT_UPDATE_OK, departmentService.updateDepartmentName(departmentIdx, updateDepartmentRequest));
    }

    @DeleteMapping("/{departmentIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "학과 삭제(관리자 전용) API", description = "학과를 soft 삭제합니다.(관리자 전용)")
    public BaseResponse<String> deleteDepartment(@PathVariable("departmentIdx") Integer departmentIdx) {
        return BaseResponse.of(DEPARTMENT_DELETE_OK, departmentService.deleteDepartment(departmentIdx));
    }
}
