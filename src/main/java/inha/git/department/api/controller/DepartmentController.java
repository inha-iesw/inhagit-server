package inha.git.department.api.controller;

import inha.git.common.BaseResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import inha.git.department.api.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.DEPARTMENT_CREATE_OK;

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

    //추후에 어드민만 접근할 수 있도록 수정해야함
    /**
     * 학과 생성
     *
     * @param createDepartmentRequest 학과 생성 요청
     * @return 생성된 학과 이름
     */
    @PostMapping
    @Operation(summary = "학과 생성", description = "학과를 생성합니다.")
    public BaseResponse<String> createDepartment(@Validated @RequestBody CreateDepartmentRequest createDepartmentRequest) {
        return BaseResponse.of(DEPARTMENT_CREATE_OK, departmentService.createDepartment(createDepartmentRequest));
    }

}
