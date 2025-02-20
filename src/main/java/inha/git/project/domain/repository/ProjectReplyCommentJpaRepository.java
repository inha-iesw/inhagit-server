package inha.git.project.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

/**
 * ProjectReplyCommentJpaRepository는 Project 대댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectReplyCommentJpaRepository extends JpaRepository<ProjectReplyComment, Integer> {
    Optional<ProjectReplyComment> findByIdAndState(Integer replyCommentIdx, State state);
    boolean existsByProjectCommentAndState(ProjectComment projectComment, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT c FROM ProjectReplyComment c WHERE c.id = :replyCommentIdx AND c.state = :state")
    Optional<ProjectReplyComment> findByIdAndStateWithPessimisticLock(Integer replyCommentIdx, State state);
}
