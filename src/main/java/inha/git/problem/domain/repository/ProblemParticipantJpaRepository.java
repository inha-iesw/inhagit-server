package inha.git.problem.domain.repository;

import inha.git.problem.domain.ProblemParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProblemParticipantJpaRepository는 ProblemParticipant 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemParticipantJpaRepository extends JpaRepository<ProblemParticipant, Integer> {
}
