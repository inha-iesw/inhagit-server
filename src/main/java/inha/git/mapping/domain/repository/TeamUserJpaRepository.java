package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.TeamUser;
import inha.git.mapping.domain.id.TeamUserId;
import inha.git.team.domain.Team;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * TeamUserJpaRepository는 TeamUser 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamUserJpaRepository extends JpaRepository<TeamUser, TeamUserId> {

    boolean existsByTeamAndUser(Team team, User user);

    Optional<TeamUser> findByUserAndTeam(User user, Team team);

    List<TeamUser> findByTeam(Team team);
}
