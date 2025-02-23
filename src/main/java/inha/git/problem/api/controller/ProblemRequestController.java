package inha.git.problem.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.request.CreateProblemApproveRequest;
import inha.git.problem.api.controller.dto.request.CreateRequestProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateRequestProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemParticipantsResponse;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.problem.api.service.ProblemRequestService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.COMPANY_PROFESSOR_CANNOT_PARTICIPATE;
import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_REQUEST_OK;
import static inha.git.common.code.status.SuccessStatus.PROBLEM_REQUEST_UPDATE_OK;

@Slf4j
@Tag(name = "problem request controller", description = "problem 신청 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems/requests")
public class ProblemRequestController {

    private final ProblemRequestService problemRequestService;

    /**
     * 문제 신청 목록 조회 API
     *
     * @param page 페이지
     * @param size 사이즈
     * @return 문제 신청 목록
     */
    @GetMapping("/requests")
    @Operation(summary = "문제 신청 목록 조회 API", description = "문제 신청 목록을 조회합니다.")
    public BaseResponse<Page<SearchRequestProblemResponse>> getRequestProblems(
            @RequestParam("problemIdx") Integer problemIdx,
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        if (size < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(PROBLEM_REQUEST_UPDATE_OK, problemRequestService.getRequestProblems(problemIdx, page - 1, size - 1));
    }

    /**
     * 문제 신청 API
     *
     * @param user 유저 정보
     * @param createRequestProblemRequest 문제 신청 요청 정보
     * @param file 파일
     * @return 신청된 문제 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문제 신청 API", description = "문제를 신청합니다.")
    public BaseResponse<RequestProblemResponse> requestProblem(@AuthenticationPrincipal User user,
                                                               @Validated @RequestPart("createRequestProblemRequest") CreateRequestProblemRequest createRequestProblemRequest,
                                                               @RequestPart(value = "file", required = false) MultipartFile file) {
        if(user.getRole().equals(Role.COMPANY) || user.getRole().equals(Role.PROFESSOR)) {
            throw new BaseException(COMPANY_PROFESSOR_CANNOT_PARTICIPATE);
        }
        return BaseResponse.of(PROBLEM_REQUEST_OK, problemRequestService.requestProblem(user, createRequestProblemRequest, file));
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
    @PutMapping(value = "/{problemRequestIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "문제 신청 수정 API", description = "문제 신청을 수정합니다.")
    public BaseResponse<RequestProblemResponse> updateRequestProblem(@AuthenticationPrincipal User user,
                                                                     @PathVariable("problemRequestIdx") Integer problemRequestIdx,
                                                                     @Validated @RequestPart("updateRequestProblemRequest") UpdateRequestProblemRequest updateRequestProblemRequest,
                                                                     @RequestPart(value = "file", required = false) MultipartFile file) {
        return BaseResponse.of(PROBLEM_REQUEST_UPDATE_OK, problemRequestService.updateRequestProblem(user, problemRequestIdx, updateRequestProblemRequest, file));
    }

    /**
     * 문제 참여 승인 API
     *
     * @param user 유저 정보
     * @param createProblemApproveRequest 문제 참여 승인 요청 정보
     * @return 승인된 문제 정보
     */
    @PutMapping("/requests/approve")
    @Operation(summary = "문제 참여 승인 API", description = "문제 참여를 승인합니다.")
    public BaseResponse<RequestProblemResponse> approveRequest(@AuthenticationPrincipal User user,
                                                               @Validated @RequestBody CreateProblemApproveRequest createProblemApproveRequest) {
        return BaseResponse.of(PROBLEM_REQUEST_UPDATE_OK, problemRequestService.approveRequest(user, createProblemApproveRequest));
    }

    /**
     * 문제 참여자 목록 조회 API
     *
     * @param problemIdx 문제 인덱스
     * @return 문제 참여자 목록
     */
    @GetMapping("/{problemIdx}/participants")
    @Operation(summary = "문제 참여자 목록 조회 API", description = "문제 참여자 목록을 조회합니다.")
    public BaseResponse<List<ProblemParticipantsResponse>> getParticipants(@AuthenticationPrincipal User user,
                                                                           @PathVariable("problemIdx") Integer problemIdx) {
        return BaseResponse.of(PROBLEM_REQUEST_UPDATE_OK, problemRequestService.getParticipants(user, problemIdx));
    }
}
