package inha.git.college.controller;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.service.CollegeService;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
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
 * 단과대학 관련 API를 처리하는 컨트롤러입니다.
 * 단과대학의 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@Slf4j
@Tag(name = "collage controller", description = "collage 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    /**
     * 모든 단과대학 목록을 조회합니다.
     *
     * @return 단과대학 목록을 포함한 응답
     */
    @GetMapping
    @Operation(summary = "단과대 전체 조회 API", description = "단과대 전체를 조회합니다.")
    public BaseResponse<List<SearchCollegeResponse>> getColleges() {
        return BaseResponse.of(COLLEGE_SEARCH_OK, collegeService.getColleges());
    }

    /**
     * 특정 학과가 속한 단과대학을 조회합니다.
     *
     * @param departmentIdx 조회할 학과의 식별자
     * @return 해당 학과가 속한 단과대학 정보를 포함한 응답
     * @throws BaseException DEPARTMENT_NOT_FOUND: 학과를 찾을 수 없는 경우,
     *                      COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @GetMapping("/{departmentIdx}")
    @Operation(summary = "단과대 조회 API", description = "단과대를 조회합니다.")
    public BaseResponse<SearchCollegeResponse> getCollege(@PathVariable("departmentIdx") Integer departmentIdx) {
        return BaseResponse.of(COLLEGE_DETAIL_OK, collegeService.getCollege(departmentIdx));
    }

    /**
     * 새로운 단과대학을 생성합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param createDepartmentRequest 생성할 단과대학 정보 (단과대학명)
     * @return 단과대학 생성 결과 메시지
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "단과대 생성(관리자 전용) API", description = "단과대를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createCollege(@AuthenticationPrincipal User user,
                                              @Validated @RequestBody CreateCollegeRequest createDepartmentRequest) {
        log.info("단과대 생성 - 관리자: {} 단과대 이름: {}", user.getName(), createDepartmentRequest.name());
        return BaseResponse.of(COLLEGE_CREATE_OK, collegeService.createCollege(user, createDepartmentRequest));
    }

    /**
     * 기존 단과대학의 정보를 수정합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param collegeIdx 수정할 단과대학의 식별자
     * @param updateCollegeRequest 수정할 단과대학 정보 (새로운 단과대학명)
     * @return 단과대학 수정 결과 메시지
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @PutMapping("/{collegeIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "단과대 수정(관리자 전용) API", description = "단과대를 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateCollege(@AuthenticationPrincipal User user,
                                              @PathVariable("collegeIdx") Integer collegeIdx,
                                              @Validated @RequestBody UpdateCollegeRequest updateCollegeRequest) {
        log.info("단과대 수정 - 관리자: {} 단과대 이름: {}", user.getName(), updateCollegeRequest.name());
        return BaseResponse.of(COLLEGE_UPDATE_OK, collegeService.updateCollegeName(user ,collegeIdx, updateCollegeRequest));
    }

    /**
     * 단과대학을 삭제(비활성화) 처리합니다.
     * 관리자 권한을 가진 사용자만 접근 가능합니다.
     * 실제 삭제가 아닌 소프트 삭제로 처리됩니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param collegeIdx 삭제할 단과대학의 식별자
     * @return 단과대학 삭제 결과 메시지
     * @throws BaseException COLLEGE_NOT_FOUND: 단과대학을 찾을 수 없는 경우
     */
    @DeleteMapping("/{collegeIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "단과대 삭제(관리자 전용) API", description = "단과대를 soft 삭제합니다.(관리자 전용)")
    public BaseResponse<String> deleteCollege (@AuthenticationPrincipal User user,
                                               @PathVariable("collegeIdx") Integer collegeIdx) {
        log.info("단과대 삭제 - 관리자: {} 단과대 인덱스: {}", user.getName(), collegeIdx);
        return BaseResponse.of(DEPARTMENT_DELETE_OK, collegeService.deleteCollege(user, collegeIdx));
    }
}
