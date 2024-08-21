package inha.git.team.api.mapper;

import inha.git.mapping.domain.TeamUser;
import inha.git.mapping.domain.id.TeamUserId;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.team.api.controller.dto.request.CreateTeamPostRequest;
import inha.git.team.api.controller.dto.request.CreateTeamRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamPostRequest;
import inha.git.team.api.controller.dto.request.UpdateTeamRequest;
import inha.git.team.api.controller.dto.response.SearchTeamResponse;
import inha.git.team.api.controller.dto.response.SearchTeamsResponse;
import inha.git.team.api.controller.dto.response.TeamPostResponse;
import inha.git.team.api.controller.dto.response.TeamResponse;
import inha.git.team.domain.Team;
import inha.git.team.domain.TeamPost;
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


    /**
     * 팀을 SearchTeamsResponse 응답으로 변환.
     *
     * @param team Team
     * @return SearchTeamsResponse
     */
    @Mapping(target = "idx", source = "team.id")
    @Mapping(target = "name", source = "team.name")
    SearchTeamsResponse teamToSearchTeamsResponse(Team team);
    List<SearchTeamsResponse> teamsToSearchTeamsResponse(List<Team> teams);

    /**
     * 팀 사용자를 생성.
     *
     * @param user User
     * @param team Team
     * @return TeamUser
     */
    default TeamUser createTeamUser(User user, Team team) {
        return new TeamUser(new TeamUserId(user.getId(), team.getId()), team, user, LocalDateTime.now(), LocalDateTime.now());
    }

    /**
     * 요청된 팀 사용자를 생성.
     *
     * @param user User
     * @param team Team
     * @return TeamUser
     */
    default TeamUser createRequestTeamUser(User user, Team team) {
        return new TeamUser(new TeamUserId(user.getId(), team.getId()), team, user, LocalDateTime.now(), null);
    }

    /**
     * 사용자를 SearchUserResponse으로 변환.
     *
     * @param user User
     * @return SearchUserResponse
     */
    @Mapping(target = "idx", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    SearchUserResponse userToSearchUserResponse(User user);

    /**
     * 팀을 SearchTeamResponse 으로 변환.
     *
     * @param team Team
     * @param leader SearchUserResponse
     * @return SearchTeamResponse
     */
    @Mapping(target = "idx", source = "team.id")
    @Mapping(target = "name", source = "team.name")
    @Mapping(target = "leader", source = "leader")
    @Mapping(target = "maxMember", source = "team.maxMemberNumber")
    @Mapping(target = "currentMember", source = "team.currtentMemberNumber")
    @Mapping(target = "createdAt", source = "team.createdAt")
    SearchTeamResponse teamToSearchTeamResponse(Team team, SearchUserResponse leader);

    /**
     * 팀 게시글을 생성.
     *
     * @param team Team
     * @param createTeamPostRequest CreateTeamPostRequest
     * @return TeamPost
     */
    @Mapping(target = "team", source = "team")
    @Mapping(target = "contents", source = "createTeamPostRequest.contents")
    @Mapping(target = "title", source = "createTeamPostRequest.title")
    @Mapping(target = "id", ignore = true)
    TeamPost createTeamPost(Team team, CreateTeamPostRequest createTeamPostRequest);

    /**
     * 팀 게시글을 TeamPostResponse로 변환.
     *
     * @param teamPost TeamPost
     * @return TeamPostResponse
     */
    @Mapping(target = "idx", source = "teamPost.id")
    TeamPostResponse teamPostToTeamPostResponse(TeamPost teamPost);

    /**
     * UpdateTeamPostRequest를 TeamPost로 변환.
     *
     * @param updateTeamPostRequest UpdateTeamPostRequest
     * @param teamPost TeamPost
     */
    @Mapping(target = "title", source = "updateTeamPostRequest.title")
    @Mapping(target = "contents", source = "updateTeamPostRequest.contents")
    void updateTeamPostRequestToTeamPost(UpdateTeamPostRequest updateTeamPostRequest, @MappingTarget TeamPost teamPost);
}
