package inha.git.problem.domain.repository;

import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.*;

/**
 * ProblemRequestJpaRepository는 Problem 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemRequestJpaRepository extends JpaRepository<ProblemRequest, Integer> {
    Optional<ProblemRequest> findByIdAndState(Integer integer, State state);
    List<ProblemRequest> findByProblemIdAndAcceptAtIsNotNullAndState(Integer problemId, State state);
    List<ProblemRequest> findByProblemIdAndState(Integer problemIdx, State state);
    Optional<ProblemRequest> findByProblemAndAcceptAtIsNotNullAndState(Problem problem, State state);
}
