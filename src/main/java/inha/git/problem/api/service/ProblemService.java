package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.request.CreateProblemRequest;
import inha.git.problem.api.controller.dto.request.UpdateProblemRequest;
import inha.git.problem.api.controller.dto.response.ProblemResponse;
import inha.git.user.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProblemService {
    ProblemResponse createProblem(User user, CreateProblemRequest createProblemRequest, MultipartFile file);

    ProblemResponse updateProblem(User user, Integer problemIdx, UpdateProblemRequest updateProblemRequest, MultipartFile file);

    ProblemResponse deleteProblem(User user, Integer problemIdx);
}
