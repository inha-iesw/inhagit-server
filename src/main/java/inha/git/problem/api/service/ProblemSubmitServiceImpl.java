package inha.git.problem.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.problem.api.controller.dto.response.ProblemSubmitResponse;
import inha.git.problem.api.mapper.ProblemSubmitMapper;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemSubmit;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.problem.domain.repository.ProblemRequestJpaRepository;
import inha.git.problem.domain.repository.ProblemSubmitJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.NOT_EXIST_REQUEST_PROBLEM;
import static inha.git.common.code.status.ErrorStatus.PROBLEM_SUBMIT_NOT_ALLOWED;

/**
 * ProblemSubmitServiceImpl는 ProblemSubmitService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProblemSubmitServiceImpl implements ProblemSubmitService {

    private final ProblemRequestJpaRepository problemRequestJpaRepository;
    private final ProblemSubmitJpaRepository problemSubmitJpaRepository;
    private final ProblemSubmitMapper problemSubmitMapper;

    /**
     * 문제 제출
     *
     * @param user       사용자 정보
     * @param problemIdx 문제 인덱스
     * @param projectIdx 프로젝트 인덱스
     * @return 문제 제출 결과를 포함하는 ProblemSubmitResponse
     */
    @Override
    @Transactional
    public ProblemSubmitResponse problemSubmit(User user, Integer problemIdx, Integer projectIdx) {
        ProblemRequest problemRequest = problemRequestJpaRepository.findByProblemIdAndUserAndState(problemIdx, user, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_REQUEST_PROBLEM));

        if (!problemRequest.getProblemRequestStatus().equals(ProblemRequestStatus.APPROVAL)) {
            throw new BaseException(PROBLEM_SUBMIT_NOT_ALLOWED);
        }
        ProblemSubmit problemSubmit = problemSubmitMapper.toProblemSubmit(projectIdx, problemRequest);
        problemSubmitJpaRepository.save(problemSubmit);
        problemRequest.setProblemRequestStatus(ProblemRequestStatus.COMPLETE);
        problemRequestJpaRepository.save(problemRequest);
        return problemSubmitMapper.toProblemSubmitResponse(problemSubmit);
    }
}
