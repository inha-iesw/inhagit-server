package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.request.CreateProblemApproveRequest;
import inha.git.problem.api.controller.dto.request.CreateRequestProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateRequestProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemParticipantsResponse;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProblemRequestService {
    Page<SearchRequestProblemResponse> getRequestProblems(User user, Integer problemIdx, Integer page, Integer size);
    RequestProblemResponse requestProblem(User user, CreateRequestProblemRequest createRequestProblemRequest, MultipartFile file);
    RequestProblemResponse updateRequestProblem(User user, Integer problemRequestIdx, UpdateRequestProblemRequest updateRequestProblemRequest, MultipartFile file);
    RequestProblemResponse deleteRequestProblem(User user, Integer problemRequestIdx);
    RequestProblemResponse approveRequest(User user, CreateProblemApproveRequest createProblemApproveRequest);
    List<ProblemParticipantsResponse> getParticipants(User user, Integer problemIdx);
}