package inha.git.team.api.controller;

import inha.git.common.BaseResponse;
import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamPostRequest;
import inha.git.team.api.controller.dto.response.SearchTeamPostResponse;
import inha.git.team.api.controller.dto.response.SearchTeamPostsResponse;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.team.api.service.TeamPostService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.*;

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
     * 팀 게시글 전체 조회 API
     *
     * <p>팀 게시글 전체를 조회한다.</p>
     *
     * @param page Integer
     * @return 검색된 팀 게시글 정보를 포함하는 BaseResponse<Page<SearchTeamPostsResponse>>
     */
    @GetMapping
    @Operation(summary = "팀 게시글 전체 조회 API", description = "팀 게시글 전체를 조회한다.")
    public BaseResponse<Page<SearchTeamPostsResponse>> getTeamPosts(@RequestParam("page") Integer page) {
        return BaseResponse.of(TEAM_POST_SEARCH_OK, teamPostService.getTeamPosts(page - 1));
    }

    /**
     * 팀 게시글 상세 조회 API
     *
     * <p>팀 게시글 상세를 조회한다.</p>
     *
     * @param postIdx Integer
     * @return 검색된 팀 게시글 정보를 포함하는 BaseResponse<SearchTeamPostResponse>
     */
    @GetMapping("/{postIdx}")
    @Operation(summary = "팀 게시글 상세 조회 API", description = "팀 게시글 상세를 조회한다.")
    public BaseResponse<SearchTeamPostResponse> getTeamPost(@PathVariable("postIdx") Integer postIdx) {
        return BaseResponse.of(TEAM_POST_DETAIL_OK, teamPostService.getTeamPost(postIdx));
    }
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

    /**
     * 팀 게시글 수정 API
     *
     * @param user User
     * @param postIdx Integer
     * @param updateTeamPostRequest UpdateTeamPostRequest
     * @return BaseResponse<TeamPostResponse>
     */
    @PutMapping("/{postIdx}")
    @Operation(summary = "팀 게시글 수정 API", description = "팀 게시글을 수정한다.")
    public BaseResponse<TeamPostResponse> updateTeamPost(@AuthenticationPrincipal User user,
                                                         @PathVariable("postIdx") Integer postIdx,
                                                         @Validated @RequestBody UpdateTeamPostRequest updateTeamPostRequest) {
        return BaseResponse.of(TEAM_POST_UPDATE_OK, teamPostService.updateTeamPost(user, postIdx, updateTeamPostRequest));
    }

    /**
     * 팀 게시글 삭제 API
     *
     * @param user User
     * @param postIdx Integer
     * @return BaseResponse<TeamPostResponse>
     */
    @DeleteMapping("/{postIdx}")
    @Operation(summary = "팀 게시글 삭제 API", description = "팀 게시글을 삭제한다.")
    public BaseResponse<TeamPostResponse> deleteTeamPost(@AuthenticationPrincipal User user,
                                                         @PathVariable("postIdx") Integer postIdx) {
        return BaseResponse.of(TEAM_POST_DELETE_OK, teamPostService.deleteTeamPost(user, postIdx));
    }


}
