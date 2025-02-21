package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.request.*;
import inha.git.problem.api.controller.dto.response.*;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProblemService {
    Page<SearchProblemsResponse> getProblems(Integer page, Integer size);
    SearchProblemResponse getProblem(Integer problemIdx);
    ProblemResponse createProblem(User user, CreateProblemRequest createProblemRequest, List<MultipartFile> files);
    ProblemResponse updateProblem(User user, Integer problemIdx, UpdateProblemRequest updateProblemRequest, MultipartFile file);
    ProblemResponse deleteProblem(User user, Integer problemIdx);
    Page<SearchRequestProblemResponse> getRequestProblems(Integer problemIdx, Integer page, Integer size);
    RequestProblemResponse requestUser(User user, CreateRequestProblemRequest createRequestProblemRequest);
    RequestProblemResponse requestTeam(User user, CreateTeamRequestProblemRequest createTeamRequestProblemRequest);
    RequestProblemResponse approveRequest(User user, CreateProblemApproveRequest createProblemApproveRequest);
    List<ProblemParticipantsResponse> getParticipants(User user, Integer problemIdx);
    List<SearchRequestProblemResponse> getAvailableSubmits(User user, Integer problemIdx);
    ProblemSubmitResponse submitPersonal(User user, Integer personalIdx, MultipartFile file);
    ProblemSubmitResponse submitTeam(User user, Integer teamIdx, MultipartFile file);
}
