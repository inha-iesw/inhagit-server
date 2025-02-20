package inha.git.team.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.team.domain.TeamComment;
import inha.git.team.domain.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * TeamCommentJpaRepository는 TeamComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamCommentJpaRepository extends JpaRepository<TeamComment, Integer> {
    Optional<TeamComment> findByIdAndState(Integer commentIdx, State state);
    List<TeamComment> findAllByTeamPostAndStateOrderByIdAsc(TeamPost teamPost, State state);
}
