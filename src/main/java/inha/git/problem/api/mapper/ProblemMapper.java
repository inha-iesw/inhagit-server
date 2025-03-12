package inha.git.problem.api.mapper;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProblemField;
import inha.git.mapping.domain.id.ProblemFieldId;
import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.problem.domain.*;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

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

    @Mapping(target = "title", source = "updateProblemRequest.title")
    @Mapping(target = "duration", source = "updateProblemRequest.duration")
    @Mapping(target = "contents", source = "updateProblemRequest.contents")
    void updateProblemRequestToProblem(UpdateProblemRequest updateProblemRequest, @MappingTarget Problem problem);

    @Mapping(target = "idx", source = "problem.id")
    SearchProblemResponse problemToSearchProblemResponse(Problem problem, SearchUserResponse author, List<SearchProblemAttachmentResponse> attachments, List<SearchFieldResponse> fieldList, Integer problemRequestIdx);

    @Mapping(target = "id", ignore = true)
    ProblemAttachment createProblemAttachmentRequestToProblemAttachment(String originalFileName, String storedFileUrl, Problem problem);

    default ProblemField createProblemField(Problem problem, Field field) {
        return new ProblemField(new ProblemFieldId(problem.getId(), field.getId()), problem, field);
    }
}
