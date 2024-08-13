package inha.git.team.domain.repository;


import inha.git.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * TeamJpaRepository는 Team 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamJpaRepository extends JpaRepository<Team, Integer> {


}