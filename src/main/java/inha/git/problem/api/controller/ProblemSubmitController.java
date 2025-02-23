package inha.git.problem.api.controller;

import inha.git.common.BaseResponse;
import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.problem.api.service.ProblemSubmitService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.PROBLEM_SUBMIT_OK;

/**
 * ProblemSubmitController는 problem submit 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "problem submit controller", description = "problem submit 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/problems")
public class ProblemSubmitController {

    private final ProblemSubmitService problemSubmitService;

    /**
     * 문제 제출 API
     *
     * <p>문제를 제출합니다.</p>
     *
     * @param user 사용자 정보
     * @param problemIdx 문제 인덱스
     * @param projectIdx 프로젝트 인덱스
     * @return 문제 제출 결과를 포함하는 BaseResponse<ProblemSubmitResponse>
     */
    @PostMapping("/{problemIdx}/submits")
    @Operation(summary = "문제 제출 API", description = "문제를 제출합니다.")
    public BaseResponse<ProblemSubmitResponse> problemSubmit(@AuthenticationPrincipal User user,
                                                             @PathVariable("problemIdx") Integer problemIdx,
                                                             @RequestParam("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PROBLEM_SUBMIT_OK, problemSubmitService.problemSubmit(user, problemIdx, projectIdx));
    }
}
