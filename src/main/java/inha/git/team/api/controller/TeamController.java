package inha.git.team.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.ErrorStatus.COMPANY_CANNOT_CREATE_TEAM;
import static inha.git.common.code.status.SuccessStatus.TEAM_CREATE_OK;

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

}
