package inha.git.problem.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.request.CreateRequestProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateRequestProblemRequest;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemsResponse;
import inha.git.problem.api.service.ProblemRequestService;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.PagingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.ErrorStatus.COMPANY_PROFESSOR_CANNOT_PARTICIPATE;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_PARTICIPANT_STATE_CHANGE_OK;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_REQUEST_DETAIL_OK;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_REQUEST_OK;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_REQUEST_SEARCH_OK;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_REQUEST_UPDATE_OK;

@Slf4j
@Tag(name = "problem request controller", description = "problem 신청 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemRequestController {

    private final ProblemRequestService problemRequestService;

    /**
     * 문제 신청 목록 조회 API
     *
     * @param page 페이지
     * @param size 사이즈
     * @return 문제 신청 목록
     */
    @GetMapping("/{problemIdx}/requests")
    @Operation(summary = "문제 신청 목록 조회 API", description = "문제 신청 목록을 조회합니다.")
    public BaseResponse<Page<SearchRequestProblemsResponse>> getRequestProblems(
            @AuthenticationPrincipal User user,
            @PathVariable("problemIdx") Integer problemIdx,
            @RequestParam(value = "problemRequestStatus", required = false) ProblemRequestStatus problemRequestStatus,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size) {
        PagingUtils.validatePage(page, size);
        return BaseResponse.of(PROBLEM_REQUEST_SEARCH_OK, problemRequestService.getRequestProblems(user, problemRequestStatus, problemIdx, PagingUtils.toPageIndex(page), size));
    }

    /**
     * 문제 신청 조회 API
     *
     * @param problemIdx 문제 인덱스
     * @param problemRequestIdx 문제 신청 인덱스
     * @return 문제 신청 정보
     */
    @GetMapping("/{problemIdx}/requests/{problemRequestIdx}")
    @Operation(summary = "문제 신청 조회 API", description = "문제 신청을 조회합니다.")
    public BaseResponse<SearchRequestProblemResponse> getRequestProblem(@AuthenticationPrincipal User user,
                                                                        @PathVariable("problemIdx") Integer problemIdx,
                                                                        @PathVariable("problemRequestIdx") Integer problemRequestIdx) {
        return BaseResponse.of(PROBLEM_REQUEST_DETAIL_OK, problemRequestService.getRequestProblem(user, problemIdx, problemRequestIdx));
    }

    /**
     * 문제 신청 API
     *
     * @param user 유저 정보
     * @param createRequestProblemRequest 문제 신청 요청 정보
     * @param file 파일
     * @return 신청된 문제 정보
     */
    @PostMapping(value = "/{problemIdx}/requests", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문제 신청 API", description = "문제를 신청합니다.")
    public BaseResponse<RequestProblemResponse> requestProblem(@AuthenticationPrincipal User user,
                                                               @PathVariable("problemIdx") Integer problemIdx,
                                                               @Validated @RequestPart("createRequestProblemRequest") CreateRequestProblemRequest createRequestProblemRequest,
                                                               @RequestPart(value = "file", required = false) MultipartFile file) {
        if(user.getRole().equals(Role.COMPANY) || user.getRole().equals(Role.PROFESSOR)) {
            throw new BaseException(COMPANY_PROFESSOR_CANNOT_PARTICIPATE);
        }
        return BaseResponse.of(PROBLEM_REQUEST_OK, problemRequestService.requestProblem(user, problemIdx, createRequestProblemRequest, file));
    }

    /**
     * 문제 신청 수정 API
     *
     * @param user 유저 정보
     * @param problemRequestIdx 문제 신청 인덱스
     * @param updateRequestProblemRequest 문제 신청 수정 요청 정보
     * @param file 파일
     * @return 수정된 문제 정보
     */
    @PutMapping(value = "/{problemIdx}/requests/{problemRequestIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문제 신청 수정 API", description = "문제 신청을 수정합니다.")
    public BaseResponse<RequestProblemResponse> updateRequestProblem(@AuthenticationPrincipal User user,
                                                                     @PathVariable("problemRequestIdx") Integer problemRequestIdx,
                                                                     @Validated @RequestPart("updateRequestProblemRequest") UpdateRequestProblemRequest updateRequestProblemRequest,
                                                                     @RequestPart(value = "file", required = false) MultipartFile file) {
        return BaseResponse.of(PROBLEM_REQUEST_UPDATE_OK, problemRequestService.updateRequestProblem(user, problemRequestIdx, updateRequestProblemRequest, file));
    }

    @DeleteMapping("/{problemIdx}/requests/{problemRequestIdx}")
    @Operation(summary = "문제 신청 삭제 API", description = "문제 신청을 삭제합니다.")
    public BaseResponse<RequestProblemResponse> deleteRequestProblem(@AuthenticationPrincipal User user,
                                                                     @PathVariable("problemRequestIdx") Integer problemRequestIdx) {
        return BaseResponse.of(PROBLEM_REQUEST_UPDATE_OK, problemRequestService.deleteRequestProblem(user, problemRequestIdx));
    }

    /**
     * 문제 신청 상태 변경 API
     *
     * @param user 유저 정보
     * @param problemIdx 문제 인덱스
     * @param problemRequestIdx 문제 신청 인덱스
     * @param problemRequestStatus 문제 신청 상태
     * @return 변경된 문제 신청 정보
     */
    @PatchMapping("/{problemIdx}/requests/{problemRequestIdx}")
    @Operation(summary = "문제 신청 상태 변경 API", description = "문제 신청 상태를 변경합니다.")
    public BaseResponse<RequestProblemResponse> updateproblemRequestStatus(@AuthenticationPrincipal User user,
                                                               @PathVariable("problemIdx") Integer problemIdx,
                                                               @PathVariable("problemRequestIdx") Integer problemRequestIdx,
                                                               @RequestParam("problemRequestStatus") ProblemRequestStatus problemRequestStatus) {
        return BaseResponse.of(PROBLEM_PARTICIPANT_STATE_CHANGE_OK, problemRequestService.updateproblemRequestStatus(user, problemIdx, problemRequestIdx, problemRequestStatus));
    }
}
