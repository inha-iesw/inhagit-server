package inha.git.team.domain.repository;


import inha.git.team.domain.TeamComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * TeamCommentJpaRepository는 TeamComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamCommentJpaRepository extends JpaRepository<TeamComment, Integer> {


}
