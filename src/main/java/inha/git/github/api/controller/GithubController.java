package inha.git.github.api.controller;

import inha.git.common.BaseResponse;
import inha.git.github.api.controller.dto.request.GitubTokenResquest;
import inha.git.github.api.service.GithubService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.GITHUB_TOKEN_REFRESH_OK;

@Slf4j
@Tag(name = "github controller", description = "github 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/github")
public class GithubController {

    private final GithubService githubService;

    @PutMapping("/token")
    @Operation(summary = "Github Token 갱신 API", description = "Github Token을 갱신합니다.")
    public BaseResponse<String> updateGithubToken(@AuthenticationPrincipal User user,
                                                  @Validated @RequestBody GitubTokenResquest gitubTokenResquest) {
        return BaseResponse.of(GITHUB_TOKEN_REFRESH_OK, githubService.updateGithubToken(user, gitubTokenResquest));
    }
}
