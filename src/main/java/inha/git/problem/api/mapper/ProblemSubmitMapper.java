package inha.git.problem.api.mapper;

import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemSubmitResponse;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemSubmit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * ProblemSubmitMapper는 ProblemSubmit 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemSubmitMapper {

    @Mapping(target = "id", ignore = true)
    ProblemSubmit toProblemSubmit(Integer projectId, ProblemRequest problemRequest);

    @Mapping(target = "idx", source = "id")
    ProblemSubmitResponse toProblemSubmitResponse(ProblemSubmit problemSubmit);

    @Mapping(target = "idx", source = "id")
    @Mapping(target = "problemidx", source = "problemRequest.problem.id")
    @Mapping(target = "problemRequestidx", source = "problemRequest.id")
    @Mapping(target = "projectidx", source = "projectId")
    SearchProblemSubmitResponse toSearchProblemSubmitResponse(ProblemSubmit problemSubmit);

    List<SearchProblemSubmitResponse> toSearchProblemSubmitResponseList(List<ProblemSubmit> problemSubmits);
}
