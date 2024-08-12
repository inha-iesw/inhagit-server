package inha.git.problem.domain.repository;


import inha.git.problem.domain.ProblemTeamlRequest;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProblemTeamRequestJpaRepository는 Problem 팀 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemTeamRequestJpaRepository extends JpaRepository<ProblemTeamlRequest, Integer> {


}
