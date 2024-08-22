package inha.git.problem.domain.repository;


import inha.git.problem.domain.ProblemRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static inha.git.common.BaseEntity.*;

/**
 * ProblemRequestJpaRepository는 Problem 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemRequestJpaRepository extends JpaRepository<ProblemRequest, Integer> {

    Optional<ProblemRequest> findByIdAndState(Integer integer, State state);
}
