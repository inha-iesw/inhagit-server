package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.user.domain.User;

public interface ProblemSubmitService {
    ProblemSubmitResponse problemSubmit(User user, Integer problemIdx, Integer projectIdx);
}
