package inha.git.team.api.mapper;

import inha.git.common.BaseEntity;
import inha.git.mapping.domain.TeamUser;
import inha.git.mapping.domain.id.TeamUserId;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.question.api.controller.dto.request.SearchReplyCommentResponse;
import inha.git.team.api.controller.dto.request.*;
import inha.git.team.api.controller.dto.response.*;
import inha.git.team.domain.Team;
import inha.git.team.domain.TeamComment;
import inha.git.team.domain.TeamPost;
import inha.git.team.domain.TeamReplyComment;
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

    @Mapping(target = "idx", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    SearchTeamUserResponse userToTeamSearchUserResponse(User user);

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

    /**
     * 팀 게시글을 SearchTeamPostResponse로 변환.
     *
     * @param teamPost TeamPost
     * @param team Team
     * @param user User
     * @return SearchTeamPostResponse
     */
    @Mapping(target = "idx", source = "teamPost.id")
    @Mapping(target = "title", source = "teamPost.title")
    @Mapping(target = "contents", source = "teamPost.contents")
    @Mapping(target = "createdAt", source = "teamPost.createdAt")
    @Mapping(target = "team", expression = "java(teamToSearchTeamResponse(team, userToSearchUserResponse(user)))")
    SearchTeamPostResponse teamPostToSearchTeamPostResponse(TeamPost teamPost, Team team, User user);

    /**
     * CreateCommentRequest를 TeamComment로 변환.
     *
     * @param createCommentRequest CreateCommentRequest
     * @param user User
     * @param teamPost TeamPost
     * @return TeamComment
     */
    @Mapping(target = "contents", source = "createCommentRequest.contents")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "teamPost", source = "teamPost")
    TeamComment toTeamComment(CreateCommentRequest createCommentRequest, User user, TeamPost teamPost);

    /**
     * 팀 댓글을 TeamCommentResponse로 변환.
     *
     * @param teamComment TeamComment
     * @return TeamCommentResponse
     */
    @Mapping(target = "idx", source = "teamComment.id")
    TeamCommentResponse toTeamCommentResponse(TeamComment teamComment);

    /**
     * updateCommentRequest를 TeamComment로 업데이트.
     *
     * @param updateCommentRequest UpdateCommentRequest
     * @param teamComment TeamComment
     */
    @Mapping(target = "contents", source = "updateCommentRequest.contents")
    void updateTeamCommentRequestToTeamComment(UpdateCommentRequest updateCommentRequest, @MappingTarget TeamComment teamComment);

    /**
     * CreateReplyCommentRequest를 TeamReplyComment로 변환.
     *
     * @param createReplyCommentRequest CreateReplyCommentRequest
     * @param user User
     * @param teamComment TeamComment
     * @return TeamReplyComment
     */
    @Mapping(target = "contents", source = "createReplyCommentRequest.contents")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "teamComment", source = "teamComment")
    TeamReplyComment toTeamReplyComment(CreateReplyCommentRequest createReplyCommentRequest, User user, TeamComment teamComment);

    /**
     * 팀 대댓글을 TeamReplyCommentResponse로 변환.
     *
     * @param teamReplyComment TeamReplyComment
     * @return TeamReplyCommentResponse
     */
    @Mapping(target = "idx", source = "teamReplyComment.id")
    TeamReplyCommentResponse toTeamReplyCommentResponse(TeamReplyComment teamReplyComment);

    /**
     * updateCommentRequest를 TeamReplyComment로 업데이트.
     *
     * @param updateCommentRequest UpdateCommentRequest
     * @param teamReplyComment TeamReplyComment
     */
    @Mapping(target = "contents", source = "updateCommentRequest.contents")
    void updateTeamReplyCommentRequestToTeamReplyComment(UpdateCommentRequest updateCommentRequest, @MappingTarget TeamReplyComment teamReplyComment);

    /**
     * TeamComment를 CommentWithRepliesResponse로 변환합니다.
     *
     * @param teamComment TeamComment
     * @return CommentWithRepliesResponse
     */
    @Mapping(target = "idx", source = "teamComment.id")
    @Mapping(target = "author", source = "teamComment.user")
    @Mapping(target = "createdAt", source = "teamComment.createdAt")
    @Mapping(target = "contents", source = "teamComment.contents")
    @Mapping(target = "replies", expression = "java(filterActiveReplies(teamComment.getReplies()))")
    CommentWithRepliesResponse toCommentWithRepliesResponse(TeamComment teamComment);

    /**
     * TeamReplyComment를 SearchReplyCommentResponse로 변환합니다.
     *
     * @param teamReplyComment TeamReplyComment
     * @return SearchReplyCommentResponse
     */
    @Mapping(target = "idx", source = "teamReplyComment.id")
    @Mapping(target = "author", source = "teamReplyComment.user")
    @Mapping(target = "createdAt", source = "teamReplyComment.createdAt")
    @Mapping(target = "contents", source = "teamReplyComment.contents")
    SearchReplyCommentResponse toSearchReplyCommentResponse(TeamReplyComment teamReplyComment);

    /**
     * TeamComment 목록을 CommentResponse 목록으로 변환.
     *
     * @param replies List<TeamComment>
     * @return List<CommentResponse>
     */
    default List<SearchReplyCommentResponse> filterActiveReplies(List<TeamReplyComment> replies) {
        return replies.stream()
                .filter(reply -> reply.getState() == BaseEntity.State.ACTIVE)  // 상태가 ACTIVE인 대댓글만 필터링
                .map(this::toSearchReplyCommentResponse)  // SearchReplyCommentResponse로 매핑
                .toList();
    }

    /**
     * TeamComment 목록을 CommentResponse 목록으로 변환.
     *
     * @param replies List<TeamComment>
     * @return List<CommentResponse>
     */
    List<SearchReplyCommentResponse> toSearchReplyCommentResponseList(List<TeamComment> replies);

    /**
     * TeamComment 목록을 CommentWithRepliesResponse 목록으로 변환합니다.
     *
     * @param comments List<TeamComment>
     * @return List<CommentWithRepliesResponse>
     */
    List<CommentWithRepliesResponse> toCommentWithRepliesResponseList(List<TeamComment> comments);
}
