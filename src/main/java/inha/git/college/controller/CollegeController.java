package inha.git.college.controller;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.service.CollegeService;
import inha.git.common.BaseResponse;
import inha.git.department.api.controller.dto.request.CreateDepartmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * CollegeController는 collage 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "collage controller", description = "collage 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/collages")
public class CollegeController {

    private final CollegeService collegeService;

    /**
     * 단과대 생성 API
     *
     * @param createDepartmentRequest 단과대 생성 요청
     * @return 생성된 단과대 이름
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "단과대 생성(관리자 전용) API", description = "단과대를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createDepartment(@Validated @RequestBody CreateCollegeRequest createDepartmentRequest) {
        return BaseResponse.of(COLLEGE_CREATE_OK, collegeService.createCollege(createDepartmentRequest));
    }

    /**
     * 단과대 수정 API
     *
     * @param collegeIdx 단과대 인덱스
     * @param updateCollegeRequest 단과대 수정 요청
     * @return 수정된 단과대 이름
     */
    @PutMapping("/{collegeIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "단과대 수정(관리자 전용) API", description = "단과대를 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateDepartment(@PathVariable("collegeIdx") Integer collegeIdx,
                                                 @Validated @RequestBody UpdateCollegeRequest updateCollegeRequest) {
        return BaseResponse.of(COLLEGE_UPDATE_OK, collegeService.updateCollegeName(collegeIdx, updateCollegeRequest));
    }


}
