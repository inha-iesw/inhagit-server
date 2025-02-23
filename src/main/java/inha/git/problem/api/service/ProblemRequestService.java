package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.request.CreateRequestProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateRequestProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemParticipantsResponse;
import inha.git.problem.api.controller.dto.response.RequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemsResponse;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProblemRequestService {
    Page<SearchRequestProblemsResponse> getRequestProblems(User user, ProblemRequestStatus problemRequestStatus, Integer problemIdx, Integer page, Integer size);
    SearchRequestProblemResponse getRequestProblem(User user, Integer problemIdx, Integer problemRequestIdx);
    RequestProblemResponse requestProblem(User user, Integer problemIdx, CreateRequestProblemRequest createRequestProblemRequest, MultipartFile file);
    RequestProblemResponse updateRequestProblem(User user, Integer problemRequestIdx, UpdateRequestProblemRequest updateRequestProblemRequest, MultipartFile file);
    RequestProblemResponse deleteRequestProblem(User user, Integer problemRequestIdx);
    RequestProblemResponse updateproblemRequestStatus(User user, Integer problemIdx, Integer problemRequestIdx, ProblemRequestStatus problemRequestStatus);
}
