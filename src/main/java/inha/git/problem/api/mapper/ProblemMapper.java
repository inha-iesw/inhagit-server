package inha.git.problem.api.mapper;

import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.domain.Problem;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * ProblemMapper는 Problem 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "filePath", source = "filePath")
    Problem createProblemRequestToProblem(CreateProblemRequest createProblemRequest,String filePath, User user);

    @Mapping(target = "idx", source = "problem.id")
    ProblemResponse problemToProblemResponse(Problem problem);
}
