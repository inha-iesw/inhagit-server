package inha.git.team.domain.repository;


import inha.git.team.domain.TeamReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * TeamReplyCommentJpaRepository는 TeamReplyComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamReplyCommentJpaRepository extends JpaRepository<TeamReplyComment, Integer> {


}
