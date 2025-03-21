package inha.git.problem.api.mapper;

import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.department.domain.Department;
import inha.git.problem.api.controller.dto.request.CreateProblemParticipantRequest;
import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.CreateRequestProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemParticipantRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemParticipantResponse;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemAttachmentResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchUserRequestProblemResponse;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemAttachment;
import inha.git.problem.domain.ProblemParticipant;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemSubmit;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * ProblemRequestMapper는 ProblemRequest 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProblemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problemRequestStatus", constant = "REQUEST")
    @Mapping(target = "title", source = "createRequestProblemRequest.title")
    @Mapping(target = "contents", source = "createRequestProblemRequest.contents")
    @Mapping(target = "team", source = "createRequestProblemRequest.team")
    @Mapping(target = "problem", source = "problem")
    @Mapping(target = "user", source = "user")
    ProblemRequest toProblemRequest(CreateRequestProblemRequest createRequestProblemRequest, Problem problem, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problemRequest", source = "problemRequest")
    ProblemParticipant toProblemParticipant(CreateProblemParticipantRequest request, ProblemRequest problemRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "problemRequest", source = "problemRequest")
    ProblemParticipant toProblemParticipant(UpdateProblemParticipantRequest request, ProblemRequest problemRequest);

    @Mapping(target = "idx", source = "id")
    RequestProblemResponse toRequestProblemResponse(ProblemRequest savedProblemRequest);

    @Mapping(target = "idx", source = "problemRequest.id")
    @Mapping(target = "user", source = "problemRequest.user")
    @Mapping(target = "participants", source = "problemRequest.problemParticipants")
    @Mapping(target = "projectIdx", source = "projectIdx")
    SearchRequestProblemResponse toSearchRequestProblemResponse(ProblemRequest problemRequest, Integer projectIdx);

    @Mapping(target = "idx", source = "id")
    SearchUserRequestProblemResponse toSearchUserRequestProblemResponse(User user);

    @Mapping(target = "idx", source = "id")
    @Mapping(target = "department", source = "department")
    ProblemParticipantResponse toProblemParticipantResponse(ProblemParticipant participant);

    @Mapping(target = "idx", source = "id")
    SearchDepartmentResponse toSearchDepartmentResponse(Department department);

    List<ProblemParticipantResponse> toProblemParticipantResponseList(List<ProblemParticipant> participants);

}
