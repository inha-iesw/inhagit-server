package inha.git.problem.api.controller;

import inha.git.common.BaseResponse;
import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.service.ProblemService;
import inha.git.user.domain.User;
import inha.git.utils.file.ValidFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.SuccessStatus.PROBLEM_CREATE_OK;

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


}
