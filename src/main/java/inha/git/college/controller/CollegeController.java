package inha.git.college.controller;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.service.CollegeService;
import inha.git.common.BaseResponse;
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
 * CollegeController는 collage 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "collage controller", description = "collage 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    /**
     * 단과대 전체 조회 API
     *
     * <p>단과대 전체를 조회합니다.</p>
     *
     * @return 단과대 전체 조회 결과를 포함하는 BaseResponse<List<SearchCollegeResponse>>
     */
    @GetMapping
    @Operation(summary = "단과대 전체 조회 API", description = "단과대 전체를 조회합니다.")
    public BaseResponse<List<SearchCollegeResponse>> getColleges() {
        return BaseResponse.of(COLLEGE_SEARCH_OK, collegeService.getColleges());
    }

    /**
     * 단과대 조회 API
     *
     * @param departmentIdx 단과대 인덱스
     * @return 단과대 조회 결과
     */
    @GetMapping("/{departmentIdx}")
    @Operation(summary = "단과대 조회 API", description = "단과대를 조회합니다.")
    public BaseResponse<SearchCollegeResponse> getCollege(@PathVariable("departmentIdx") Integer departmentIdx) {
        return BaseResponse.of(COLLEGE_DETAIL_OK, collegeService.getCollege(departmentIdx));
    }

    /**
     * 단과대 생성 API
     *
     * @param createDepartmentRequest 단과대 생성 요청
     * @return 생성된 단과대 이름
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "단과대 생성(관리자 전용) API", description = "단과대를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createCollege(@Validated @RequestBody CreateCollegeRequest createDepartmentRequest) {
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
    public BaseResponse<String> updateCollege(@PathVariable("collegeIdx") Integer collegeIdx,
                                                 @Validated @RequestBody UpdateCollegeRequest updateCollegeRequest) {
        return BaseResponse.of(COLLEGE_UPDATE_OK, collegeService.updateCollegeName(collegeIdx, updateCollegeRequest));
    }

    /**
     * 단과대 삭제 API
     *
     * @param collegeIdx 단과대 인덱스
     * @return 삭제된 단과대 이름
     */
    @DeleteMapping("/{collegeIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "단과대 삭제(관리자 전용) API", description = "단과대를 soft 삭제합니다.(관리자 전용)")
    public BaseResponse<String> deleteCollege (@PathVariable("collegeIdx") Integer collegeIdx) {
        return BaseResponse.of(DEPARTMENT_DELETE_OK, collegeService.deleteCollege(collegeIdx));
    }

}
