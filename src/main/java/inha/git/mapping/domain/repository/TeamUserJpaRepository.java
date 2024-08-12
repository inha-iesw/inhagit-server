package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.TeamUser;
import inha.git.mapping.domain.id.TeamUserId;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * TeamUserJpaRepository는 TeamUser 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamUserJpaRepository extends JpaRepository<TeamUser, TeamUserId> {


}
