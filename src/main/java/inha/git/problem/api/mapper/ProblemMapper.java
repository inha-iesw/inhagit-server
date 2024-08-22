package inha.git.problem.api.mapper;

import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemResponse;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemPersonalRequest;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemTeamRequest;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.team.domain.Team;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * ProblemMapper는 Problem 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemMapper {

    /**
     * CreateProblemRequest를 Problem 엔티티로 변환
     * @param createProblemRequest
     * @param filePath
     * @param user
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "filePath", source = "filePath")
    Problem createProblemRequestToProblem(CreateProblemRequest createProblemRequest,String filePath, User user);

    /**
     * Problem 엔티티를 ProblemResponse로 변환
     * @param problem
     * @return
     */
    @Mapping(target = "idx", source = "problem.id")
    ProblemResponse problemToProblemResponse(Problem problem);

    /**
     * 파일 경로가 없는 경우
     * @param updateProblemRequest
     * @param problem
     */
    @Mapping(target = "title", source = "updateProblemRequest.title")
    @Mapping(target = "duration", source = "updateProblemRequest.duration")
    @Mapping(target = "contents", source = "updateProblemRequest.contents")
    void updateProblemRequestToProblem(UpdateProblemRequest updateProblemRequest, @MappingTarget Problem problem);

    /**
     * 파일 경로가 있는 경우
     * @param updateProblemRequest
     * @param filePath
     * @param problem
     */
    @Mapping(target = "title", source = "updateProblemRequest.title")
    @Mapping(target = "duration", source = "updateProblemRequest.duration")
    @Mapping(target = "contents", source = "updateProblemRequest.contents")
    @Mapping(target = "filePath", source = "filePath") // 새로운 파일 경로를 매핑
    void updateProblemRequestToProblem(UpdateProblemRequest updateProblemRequest, String filePath, @MappingTarget Problem problem);

    /**
     * Problem 엔티티를 SearchProblemResponse로 변환
     * @param problem
     * @param author
     * @return
     */
    @Mapping(target = "idx", source = "problem.id")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "createdAt", source = "problem.createdAt")
    SearchProblemResponse problemToSearchProblemResponse(Problem problem, User author);

    /**
     * User 엔티티를 SearchUserResponse로 변환
     * @param user
     * @return
     */
    @Mapping(target = "idx", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    SearchUserResponse userToSearchUserResponse(User user);

    /**
     * User와 Problem을 ProblemRequest로 변환
     * @param user
     * @param problem
     * @param type
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problem", source = "problem")
    @Mapping(target = "type", source = "type")
    ProblemRequest createProblemRequestToProblemRequest(Problem problem, Integer type);

    /**
     * ProblemReuqest를 RequestProblemResponse로 변환
     * @param problemRequest
     * @return
     */
    @Mapping(target = "idx", source = "problemRequest.id")
    RequestProblemResponse problemRequestToRequestProblemResponse(ProblemRequest problemRequest);

    /**
     * User와 ProblemRequest를 ProblemPersonalRequest로 변환
     * @param user
     * @param problemRequest
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "problemRequest", source = "problemRequest")
    @Mapping(target = "problem", source = "problemRequest.problem")
    ProblemPersonalRequest createRequestProblemRequestToProblemPersonalRequest(User user, ProblemRequest problemRequest);

    /**
     * Team과 ProblemRequest를 ProblemTeamRequest로 변환
     * @param team
     * @param problemRequest
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "team", source = "team")
    @Mapping(target = "problemRequest", source = "problemRequest")
    @Mapping(target = "problem", source = "problemRequest.problem")
    ProblemTeamRequest createTeamRequestProblemRequestToProblemTeamRequest(Team team, ProblemRequest problemRequest);

}
