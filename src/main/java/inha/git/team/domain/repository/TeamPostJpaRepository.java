package inha.git.team.domain.repository;


import inha.git.team.domain.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * TeamPostJpaRepository는 TeamPost 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamPostJpaRepository extends JpaRepository<TeamPost, Integer> {


}
