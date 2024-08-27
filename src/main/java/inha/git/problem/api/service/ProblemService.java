package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.request.*;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProblemService {
    Page<SearchProblemsResponse> getProblems(Integer page);
    SearchProblemResponse getProblem(Integer problemIdx);
    ProblemResponse createProblem(User user, CreateProblemRequest createProblemRequest, MultipartFile file);
    ProblemResponse updateProblem(User user, Integer problemIdx, UpdateProblemRequest updateProblemRequest, MultipartFile file);
    ProblemResponse deleteProblem(User user, Integer problemIdx);
    Page<SearchRequestProblemResponse> getRequestProblems(Integer problemIdx, Integer page);
    RequestProblemResponse requestUser(User user, CreateRequestProblemRequest createRequestProblemRequest);
    RequestProblemResponse requestTeam(User user, CreateTeamRequestProblemRequest createTeamRequestProblemRequest);
    RequestProblemResponse approveRequest(User user, CreateProblemApproveRequest createProblemApproveRequest);


}
