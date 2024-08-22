package inha.git.problem.domain.repository;


import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemReuqest;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ProblemRequestJpaRepository는 Problem 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemRequestJpaRepository extends JpaRepository<ProblemReuqest, Integer> {

    Optional<ProblemReuqest> findByProblemAndUser(Problem problem, User user);
}
