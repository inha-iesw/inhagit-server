package inha.git.problem.domain.repository;


import inha.git.problem.domain.ProblemReuqest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProblemRequestJpaRepository는 Problem 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemRequestJpaRepository extends JpaRepository<ProblemReuqest, Integer> {


}
