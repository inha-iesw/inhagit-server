package inha.git.problem.api.mapper;

import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.problem.domain.*;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participantCount", constant = "0")
    @Mapping(target = "status", constant = "PROGRESS")
    Problem createProblemRequestToProblem(User user, CreateProblemRequest createProblemReques);

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
     * @param problem
     * @param type
     * @return
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problem", source = "problem")
    ProblemRequest createProblemRequestToProblemRequest(Problem problem, Integer type);

    /**
     * ProblemReuqest를 RequestProblemResponse로 변환
     * @param problemRequest
     * @return
     */
    @Mapping(target = "idx", source = "problemRequest.id")
    RequestProblemResponse problemRequestToRequestProblemResponse(ProblemRequest problemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problemRequest", source = "problemRequest")
    ProblemSubmit createProblemSubmitRequestToProblemSubmit(ProblemRequest problemRequest, String zipFilePath, String folderName);

    @Mapping(target = "idx", source = "problemSubmit.id")
    ProblemSubmitResponse problemSubmitToProblemSubmitResponse(ProblemSubmit problemSubmit);
}
