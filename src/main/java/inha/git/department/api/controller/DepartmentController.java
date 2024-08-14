package inha.git.department.api.controller;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.common.BaseResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.DEPARTMENT_CREATE_OK;
import static inha.git.common.code.status.SuccessStatus.DEPARTMENT_SEARCH_OK;

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
     * @return 학과 전체 조회 결과를 포함하는 BaseResponse<List<SearchDepartmentResponse>>
     */
    @GetMapping
    @Operation(summary = "학과 전체 조회 API", description = "학과 전체를 조회합니다.")
    public BaseResponse<List<SearchDepartmentResponse>> getDepartments() {
        return BaseResponse.of(DEPARTMENT_SEARCH_OK, departmentService.getDepartments());
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
    @Operation(summary = "학과 생성", description = "학과를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createDepartment(@Validated @RequestBody CreateDepartmentRequest createDepartmentRequest) {
        return BaseResponse.of(DEPARTMENT_CREATE_OK, departmentService.createDepartment(createDepartmentRequest));
    }

}
