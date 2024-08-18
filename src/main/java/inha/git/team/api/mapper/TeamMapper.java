package inha.git.team.api.mapper;

import inha.git.mapping.domain.TeamUser;
import inha.git.mapping.domain.id.TeamUserId;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.SearchTeamsResponse;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.team.domain.Team;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TeamMapper는 Team 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeamMapper {
    /**
     * CreateTeamRequest를 Team 엔티티로 변환.
     *
     * @param createTeamRequest CreateTeamRequest
     * @param user User
     * @return Team
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "name", source = "createTeamRequest.name")
    @Mapping(target = "maxMemberNumber", source = "createTeamRequest.maxMember")
    @Mapping(target = "currtentMemberNumber", constant = "1")
    Team createTeamRequestToTeam(CreateTeamRequest createTeamRequest, User user);

    /**
     * Team을 TeamResponse로 변환.
     *
     * @param team Team
     * @return TeamResponse
     */
    @Mapping(target = "idx", source = "team.id")
    TeamResponse teamToTeamResponse(Team team);

    /**
     * UpdateTeamRequest를 Team 엔티티로 변환.
     *
     * @param updateTeamRequest UpdateTeamRequest
     * @param team Team
     */
    @Mapping(target = "name", source = "updateTeamRequest.name")
    @Mapping(target = "maxMemberNumber", source = "updateTeamRequest.maxMember")
    void updateTeamRequestToTeam(UpdateTeamRequest updateTeamRequest, @MappingTarget Team team);


    @Mapping(target = "idx", source = "team.id")
    @Mapping(target = "name", source = "team.name")
    SearchTeamsResponse teamToSearchTeamsResponse(Team team);
    List<SearchTeamsResponse> teamsToSearchTeamsResponse(List<Team> teams);

    default TeamUser createTeamUser(User user, Team team) {
        return new TeamUser(new TeamUserId(user.getId(), team.getId()), team, user, LocalDateTime.now());
    }

}
