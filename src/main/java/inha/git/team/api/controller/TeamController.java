package inha.git.team.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.SearchTeamResponse;
import inha.git.team.api.controller.dto.response.SearchTeamsResponse;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.team.api.service.TeamService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.COMPANY_CANNOT_CREATE_TEAM;
import static inha.git.common.code.status.SuccessStatus.*;

/**
 * TeamController는 Team 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "team controller", description = "team 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/teams")
public class TeamController {
    private final TeamService teamService;

    /**
     * 내가 생성한 팀 목록 가져오기 API
     *
     * @param user User
     * @return BaseResponse<List<SearchTeamsResponse>>
     */
    @GetMapping("/my")
    @Operation(summary = "내가 생성한 팀 목록 가져오기 API", description = "내가 생성한 팀 목록을 가져옵니다.")
    public BaseResponse<List<SearchTeamsResponse>> getMyTeams(@AuthenticationPrincipal User user) {
        return BaseResponse.of(TEAM_GET_MY_TEAMS_OK, teamService.getMyTeams(user));
    }

    @GetMapping("/{teamIdx}")
    @Operation(summary = "팀 상세 조회 API", description = "팀 상세 정보를 조회합니다.")
    public BaseResponse<SearchTeamResponse> getTeam(@PathVariable("teamIdx") Integer teamIdx) {
        return BaseResponse.of(TEAM_DETAIL_OK, teamService.getTeam(teamIdx));
    }

    /**
     * 팀 생성 API
     *
     * @param user User
     * @param createTeamRequest CreateTeamRequest
     * @return BaseResponse<TeamResponse>
     */
    @PostMapping
    @Operation(summary = "팀 생성 API", description = "팀을 생성합니다.")
    public BaseResponse<TeamResponse> createTeam(
            @AuthenticationPrincipal User user,
            @Validated @RequestBody CreateTeamRequest createTeamRequest) {
        if(user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_TEAM);
        }
        return BaseResponse.of(TEAM_CREATE_OK, teamService.createTeam(user, createTeamRequest));
    }

    /**
     * 팀 수정 API
     *
     * @param user User
     * @param teamIdx Integer
     * @param updateTeamRequest UpdateTeamRequest
     * @return BaseResponse<TeamResponse>
     */
    @PutMapping("/{teamIdx}")
    @Operation(summary = "팀 수정 API", description = "팀을 수정합니다.")
    public BaseResponse<TeamResponse> updateTeam(
            @AuthenticationPrincipal User user,
            @PathVariable("teamIdx") Integer teamIdx,
            @Validated @RequestBody UpdateTeamRequest updateTeamRequest) {
        return BaseResponse.of(TEAM_UPDATE_OK, teamService.updateTeam(user, teamIdx, updateTeamRequest));
    }

    /**
     * 팀 삭제 API
     *
     * @param user User
     * @param teamIdx Integer
     * @return BaseResponse<TeamResponse>
     */
    @DeleteMapping("/{teamIdx}")
    @Operation(summary = "팀 삭제 API", description = "팀을 삭제합니다.")
    public BaseResponse<TeamResponse> deleteTeam(
            @AuthenticationPrincipal User user,
            @PathVariable("teamIdx") Integer teamIdx) {
        return BaseResponse.of(TEAM_DELETE_OK, teamService.deleteTeam(user, teamIdx));
    }

}
