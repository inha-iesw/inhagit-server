package inha.git.problem.api.controller;

import inha.git.common.BaseResponse;
import inha.git.problem.api.controller.dto.request.*;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.problem.api.service.ProblemService;
import inha.git.problem.domain.enums.ProblemStatus;
import inha.git.user.domain.User;
import inha.git.utils.PagingUtils;
import inha.git.utils.file.ValidFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * ProblemController는 problem 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "problem controller", description = "problem 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemController {

    private final ProblemService problemService;

    /**
     * 문제 목록 조회 API
     *
     * <p>문제 목록을 조회합니다.</p>
     *
     * @param page 페이지 번호
     * @param size 페이지 사이즈
     * @return 문제 목록 조회 결과를 포함하는 BaseResponse<Page<SearchProblemsResponse>>
     */
    @GetMapping
    @Operation(summary = "문제 목록 조회 API", description = "문제 목록을 조회합니다.")
    public BaseResponse<Page<SearchProblemsResponse>> getProblems(@RequestParam("page") Integer page,
                                                                  @RequestParam("size") Integer size) {
        PagingUtils.validatePage(page, size);
        return BaseResponse.of(PROBLEM_SEARCH_OK, problemService.getProblems(PagingUtils.toPageIndex(page), size));
    }

    /**
     * 문제 상세 조회 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @return 문제 상세 정보
     */
    @GetMapping("/{problemIdx}")
    @Operation(summary = "문제 상세 조회 API", description = "문제를 상세 조회합니다.")
    public BaseResponse<SearchProblemResponse> getProblem(@AuthenticationPrincipal User user,
                                                          @PathVariable("problemIdx") Integer problemIdx) {
        return BaseResponse.of(PROBLEM_DETAIL_OK, problemService.getProblem(user, problemIdx));
    }

    /**
     * 문제 생성 API
     *
     * @param user 유저 정보
     * @param createProblemRequest 문제 생성 요청 정보
     * @param files 문제 파일들
     * @return 생성된 문제 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('professor:create', 'company:create')")
    @Operation(summary = "문제 생성(교수, 기업, 관리자 전용) API", description = "문제를 생성합니다.")
    public BaseResponse<ProblemResponse> createProblem(@AuthenticationPrincipal User user,
                                                       @Validated @RequestPart("createProblemRequest") CreateProblemRequest createProblemRequest,
                                                       @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        log.info("문제 생성 - 사용자: {} 문제 제목: {}", user.getName(), createProblemRequest.title());
        return BaseResponse.of(PROBLEM_CREATE_OK, problemService.createProblem(user, createProblemRequest, files));
    }

    /**
     * 문제 수정 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param updateProblemRequest 문제 수정 요청 정보
     * @param files 문제 파일들
     * @return 수정된 문제 정보
     */
    @PutMapping(value = "/{problemIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('professor:update', 'company:update')")
    @Operation(summary = "문제 수정(교수, 기업, 관리자 전용) API", description = "문제를 수정합니다.")
    public BaseResponse<ProblemResponse> updateProblem(@AuthenticationPrincipal User user,
                                                   @PathVariable("problemIdx") Integer problemIdx,
                                                   @Validated @RequestPart("updateProblemRequest") UpdateProblemRequest updateProblemRequest,
                                                   @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return BaseResponse.of(PROBLEM_UPDATE_OK, problemService.updateProblem(user, problemIdx, updateProblemRequest, files));
    }

    /**
     * 문제 상태 수정 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param status 문제 상태
     * @return 수정된 문제 정보
     */
    @PatchMapping(value = "/{problemIdx}")
    @PreAuthorize("hasAnyAuthority('professor:update', 'company:update')")
    @Operation(summary = "문제 상태 수정(교수, 기업, 관리자 전용) API", description = "문제를 상태를 수정합니다.")
    public BaseResponse<ProblemResponse> updateProblemStatus(@AuthenticationPrincipal User user,
                                                             @PathVariable("problemIdx") Integer problemIdx,
                                                             @Validated @RequestParam("status") ProblemStatus status) {
        return BaseResponse.of(PROBLEM_UPDATE_OK, problemService.updateProblemStatus(user, problemIdx, status));
    }

    /**
     * 문제 삭제 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @return 삭제된 문제 정보
     */
    @DeleteMapping("/{problemIdx}")
    @PreAuthorize("hasAnyAuthority('professor:update', 'company:update')")
    @Operation(summary = "문제 삭제(교수, 기업, 관리자 전용) API", description = "문제를 삭제합니다.")
    public BaseResponse<ProblemResponse> deleteProblem(@AuthenticationPrincipal User user,
                                                       @PathVariable("problemIdx") Integer problemIdx) {
        return BaseResponse.of(PROBLEM_DELETE_OK, problemService.deleteProblem(user, problemIdx));
    }
}
