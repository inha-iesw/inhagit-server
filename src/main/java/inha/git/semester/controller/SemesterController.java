package inha.git.semester.controller;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.common.BaseResponse;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.service.SemesterService;
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
 * SemesterController는 semester 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "semester controller", description = "semester 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    /**
     * 학기 생성 API
     *
     * @param createDepartmentRequest 학기 생성 요청
     * @return 생성된 학기 이름
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "학기 생성(관리자 전용) API", description = "학기를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createSemester(@Validated @RequestBody CreateSemesterRequest createDepartmentRequest) {
        return BaseResponse.of(SEMESTER_CREATE_OK, semesterService.createSemester(createDepartmentRequest));
    }

    /**
     * 학기 수정 API
     *
     * @param semesterIdx 학기 인덱스
     * @param updateSemesterRequest 학기 수정 요청
     * @return 수정된 학기 이름
     */
    @PutMapping("/{semesterIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "학기 수정(관리자 전용) API", description = "학기를 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateSemester(@PathVariable("semesterIdx") Integer semesterIdx,
                                               @Validated @RequestBody UpdateSemesterRequest updateSemesterRequest) {
        return BaseResponse.of(SEMESTER_UPDATE_OK, semesterService.updateSemesterName(semesterIdx, updateSemesterRequest));
    }
}
