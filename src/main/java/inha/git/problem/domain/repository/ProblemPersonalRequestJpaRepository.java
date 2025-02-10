package inha.git.problem.domain.repository;

import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemComment;
import inha.git.problem.domain.ProblemPersonalRequest;
import inha.git.problem.domain.ProblemRequest;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * ProblemPersonalRequestJpaRepository는 Problem 개인 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemPersonalRequestJpaRepository extends JpaRepository<ProblemPersonalRequest, Integer> {
    Optional<ProblemPersonalRequest> findByProblemAndUser(Problem problem, User user);
    Optional<ProblemPersonalRequest> findByProblemRequestId(Integer id);
    Optional<ProblemPersonalRequest> findByProblemRequestIdAndUser(Integer problemRequestIdx, User user);
}
