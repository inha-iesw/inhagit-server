package inha.git.team.api.controller;

import inha.git.common.BaseResponse;
import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.team.api.service.TeamPostService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.TEAM_POST_CREATE_OK;

/**
 * TeamPostController는 Team Post 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "team post controller", description = "team post 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams/posts")
public class TeamPostController {

    private final TeamPostService teamPostService;

    /**
     * 팀 게시글 생성 API
     *
     * @param user User
     * @param createTeamPostRequest CreateTeamPostRequest
     * @return BaseResponse<TeamPostResponse>
     */
    @PostMapping
    @Operation(summary = "팀 게시글 생성 API", description = "팀 게시글을 생성한다.")
    public BaseResponse<TeamPostResponse> createTeamPost(@AuthenticationPrincipal User user,
                                                         @Validated @RequestBody CreateTeamPostRequest createTeamPostRequest) {
        return BaseResponse.of(TEAM_POST_CREATE_OK, teamPostService.createTeamPost(user, createTeamPostRequest));
    }


}
