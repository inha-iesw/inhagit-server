package inha.git.problem.domain.repository;


import inha.git.common.BaseEntity.State;
import inha.git.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * ProblemJpaRepository는 Problem 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemJpaRepository extends JpaRepository<Problem, Integer> {

    Optional<Problem> findByIdAndState(Integer problemIdx, State state);
}
