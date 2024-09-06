package inha.git.github.api.controller;

import inha.git.common.BaseResponse;
import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.github.api.controller.dto.response.GithubRepositoryResponse;
import inha.git.github.api.service.GithubService;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.SuccessStatus.GITHUB_REPOSITORIES_OK;
import static inha.git.common.code.status.SuccessStatus.GITHUB_TOKEN_REFRESH_OK;

/**
 * GithubController는 github 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "github controller", description = "github 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/github")
public class GithubController {

    private final GithubService githubService;

    /**
     * Github Token을 갱신합니다.
     *
     * @param user                사용자 정보
     * @param gitubTokenResquest Github Token 갱신 요청 정보
     * @return 갱신 성공 메시지
     */
    @PutMapping("/token")
    @Operation(summary = "Github Token 갱신 API", description = "Github Token을 갱신합니다.")
    public BaseResponse<String> updateGithubToken(@AuthenticationPrincipal User user,
                                                  @Validated @RequestBody GitubTokenResquest gitubTokenResquest) {
        return BaseResponse.of(GITHUB_TOKEN_REFRESH_OK, githubService.updateGithubToken(user, gitubTokenResquest));
    }

    /**
     * 사용자의 Github 레포지토리 목록을 조회합니다.
     *
     * @param user 사용자 정보
     * @return Github 레포지토리 목록
     */
    @GetMapping("/repositories")
    @Operation(summary = "GitHub 레포지토리 목록 조회 API", description = "사용자의 GitHub 레포지토리 목록을 조회합니다.")
    public BaseResponse<List<GithubRepositoryResponse>> getGithubRepositories(@AuthenticationPrincipal User user) {
        return BaseResponse.of(GITHUB_REPOSITORIES_OK, githubService.getGithubRepositories(user));
    }

    @GetMapping("/repository/{projectIdx}")
    @Operation(summary = "GitHub 레포지토리 내용 조회 API", description = "GitHub 레포지토리의 내용을 조회합니다.")
    public BaseResponse<List<SearchFileResponse>> getRepositoryContents(@AuthenticationPrincipal User user,
                                                                        @PathVariable("projectIdx") Integer projectIdx,
                                                                        @RequestParam(value = "path", required = false, defaultValue = "/") String path) {
            return BaseResponse.of(GITHUB_REPOSITORIES_OK, githubService.getGithubFiles(user, projectIdx, path));
        }
}
