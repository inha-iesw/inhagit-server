package inha.git.semester.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.semester.controller.dto.request.CreateSemesterRequest;
import inha.git.semester.controller.dto.request.UpdateSemesterRequest;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.service.SemesterService;
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
 * 학기 관련 API를 처리하는 컨트롤러입니다.
 * 학기의 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@Slf4j
@Tag(name = "semester controller", description = "semester 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    /**
     * 전체 학기 목록을 조회합니다.
     *
     * @return 학기 목록을 포함한 응답
     */
    @GetMapping
    @Operation(summary = "학기 전체 조회 API", description = "학기 전체를 조회합니다.")
    public BaseResponse<List<SearchSemesterResponse>> getSemesters() {
        return BaseResponse.of(SEMESTER_SEARCH_OK, semesterService.getSemesters());
    }

    /**
     * 새로운 학기를 생성합니다.<br>
     *
     * @param user 현재 인증된 관리자 정보
     * @param createSemesterRequest 생성할 학기 정보 (학기명)
     * @return 학기 생성 결과 메시지
     */
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "학기 생성(관리자 전용) API", description = "학기를 생성합니다.(관리자 전용)")
    public BaseResponse<String> createSemester(@AuthenticationPrincipal User user,
                                               @Validated @RequestBody CreateSemesterRequest createSemesterRequest) {
        log.info("학기 생성 - 관리자: {} 학기명: {}", user.getName(), createSemesterRequest.name());
        return BaseResponse.of(SEMESTER_CREATE_OK, semesterService.createSemester(user, createSemesterRequest));
    }

    /**
     * 학기명을 수정합니다.<br>
     *
     * @param user 현재 인증된 관리자 정보
     * @param semesterIdx 수정할 학기의 식별자
     * @param updateSemesterRequest 새로운 학기명
     * @return 학기명 수정 결과 메시지
     * @throws BaseException SEMESTER_NOT_FOUND: 학기를 찾을 수 없는 경우
     */
    @PutMapping("/{semesterIdx}")
    @PreAuthorize("hasAuthority('admin:update')")
    @Operation(summary = "학기 수정(관리자 전용) API", description = "학기를 수정합니다.(관리자 전용)")
    public BaseResponse<String> updateSemester(@AuthenticationPrincipal User user,
                                               @PathVariable("semesterIdx") Integer semesterIdx,
                                               @Validated @RequestBody UpdateSemesterRequest updateSemesterRequest) {
        log.info("학기 수정 - 관리자: {} 학기명: {}", user.getName(), updateSemesterRequest.name());
        return BaseResponse.of(SEMESTER_UPDATE_OK, semesterService.updateSemesterName(user, semesterIdx, updateSemesterRequest));
    }

    /**
     * 학기를 삭제(비활성화) 처리합니다.
     *
     * @param user 현재 인증된 관리자 정보
     * @param semesterIdx 삭제할 학기의 식별자
     * @return 학기 삭제 결과 메시지
     * @throws BaseException SEMESTER_NOT_FOUND: 학기를 찾을 수 없는 경우
     */
    @DeleteMapping("/{semesterIdx}")
    @PreAuthorize("hasAuthority('admin:delete')")
    @Operation(summary = "학기 삭제(관리자 전용) API", description = "학기를 삭제합니다.(관리자 전용)")
    public BaseResponse<String> deleteSemester(@AuthenticationPrincipal User user,
                                               @PathVariable("semesterIdx") Integer semesterIdx) {
        log.info("학기 삭제 - 관리자: {} 학기명: {}", user.getName(), semesterIdx);
        return BaseResponse.of(SEMESTER_DELETE_OK, semesterService.deleteSemester(user, semesterIdx));
    }
}
