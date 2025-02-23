package inha.git.problem.api.service;

import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.problem.api.controller.dto.response.SearchProblemSubmitResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface ProblemSubmitService {
    Page<SearchProblemSubmitResponse> getProblemSubmits(User user, Integer problemIdx, Integer pageIndex, Integer size);
    ProblemSubmitResponse problemSubmit(User user, Integer problemIdx, Integer projectIdx);
}
