package inha.git.problem.domain.repository;


import inha.git.problem.domain.ProblemComment;
import inha.git.problem.domain.ProblemPersonalRequest;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProblemPersonalRequestJpaRepository는 Problem 개인 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemPersonalRequestJpaRepository extends JpaRepository<ProblemPersonalRequest, Integer> {


}
