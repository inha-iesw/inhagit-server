package inha.git.team.domain.repository;

import inha.git.team.domain.TeamNote;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TeamNoteJpaRepository는 TeamNote 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamNoteJpaRepository extends JpaRepository<TeamNote, Integer> {
}
