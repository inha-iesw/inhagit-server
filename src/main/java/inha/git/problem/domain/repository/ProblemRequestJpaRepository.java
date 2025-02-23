package inha.git.problem.domain.repository;

import inha.git.common.BaseEntity;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.enums.ProblemRequestStatus;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static inha.git.common.BaseEntity.*;

/**
 * ProblemRequestJpaRepository는 Problem 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemRequestJpaRepository extends JpaRepository<ProblemRequest, Integer> {
    Optional<ProblemRequest> findByIdAndState(Integer integer, State state);

    boolean existsByProblemAndUserAndState(Problem problem, User user, State state);

    Page<ProblemRequest> findByProblemAndState(Problem problem, State state, Pageable pageable);

    Page<ProblemRequest> findByProblemAndStateAndProblemRequestStatus(Problem problem, State state, ProblemRequestStatus problemRequestStatus, Pageable pageable);

    Optional<ProblemRequest> findByProblemIdAndUserAndState(Integer problemIdx, User user, State state);
}
