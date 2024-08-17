package inha.git.problem.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.api.service.ProblemService;
import inha.git.user.domain.User;
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

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
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

    @GetMapping
    @Operation(summary = "문제 목록 조회 API", description = "문제 목록을 조회합니다.")
    public BaseResponse<Page<SearchProblemsResponse>> getProblems(@RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(PROBLEM_SEARCH_OK, problemService.getProblems(page - 1));
    }
    /**
     * 문제 생성 API
     *
     * @param user 유저 정보
     * @param createProblemRequest 문제 생성 요청 정보
     * @param file 문제 파일
     * @return 생성된 문제 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('assistant:create')")
    @Operation(summary = "문제 생성(조교, 교수, 관리자 전용) API", description = "문제를 생성합니다.")
    public BaseResponse<ProblemResponse> createProblem(@AuthenticationPrincipal User user,
                                                       @Validated @RequestPart("createProblemRequest") CreateProblemRequest createProblemRequest,
                                                       @RequestPart(value = "file") MultipartFile file) {
        ValidFile.validateImagePdfZipFile(file);
        return BaseResponse.of(PROBLEM_CREATE_OK, problemService.createProblem(user, createProblemRequest, file));
    }

    /**
     * 문제 수정 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param updateProblemRequest 문제 수정 요청 정보
     * @param file 문제 파일
     * @return 수정된 문제 정보
     */
    @PutMapping(value = "/{problemIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('assistant:update')")
    @Operation(summary = "문제 수정(조교, 교수, 관리자 전용) API", description = "문제를 수정합니다.")
    public BaseResponse<ProblemResponse> updateProblem(@AuthenticationPrincipal User user,
                                                   @PathVariable("problemIdx") Integer problemIdx,
                                                   @Validated @RequestPart("updateProblemRequest") UpdateProblemRequest updateProblemRequest,
                                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        if(!file.isEmpty()) {
            ValidFile.validateImagePdfZipFile(file);
        }
        return BaseResponse.of(PROBLEM_UPDATE_OK, problemService.updateProblem(user, problemIdx, updateProblemRequest, file));
    }

    /**
     * 문제 삭제 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @return 삭제된 문제 정보
     */
    @DeleteMapping("/{problemIdx}")
    @PreAuthorize("hasAuthority('assistant:delete')")
    @Operation(summary = "문제 삭제(조교, 교수, 관리자 전용) API", description = "문제를 삭제합니다.")
    public BaseResponse<ProblemResponse> deleteProblem(@AuthenticationPrincipal User user,
                                                       @PathVariable("problemIdx") Integer problemIdx) {
        return BaseResponse.of(PROBLEM_DELETE_OK, problemService.deleteProblem(user, problemIdx));
    }

}
