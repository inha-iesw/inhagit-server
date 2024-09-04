package inha.git.problem.domain.repository;


import inha.git.problem.domain.Problem;
import inha.git.problem.domain.ProblemTeamRequest;
import inha.git.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * ProblemTeamRequestJpaRepository는 Problem 팀 신청 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemTeamRequestJpaRepository extends JpaRepository<ProblemTeamRequest, Integer> {


    Optional<ProblemTeamRequest> findByProblemAndTeam(Problem problem, Team team);

    Optional<ProblemTeamRequest> findByProblemRequestId(Integer id);
}
