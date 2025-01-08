package inha.git.project.domain.repository;


import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.user.domain.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State;


/**
 * ProjectCommentJpaRepository는 Project 댓글 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectCommentJpaRepository extends JpaRepository<ProjectComment, Integer> {

    Optional<ProjectComment> findByIdAndState(Integer commentIdx, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT c FROM ProjectComment c WHERE c.id = :commentIdx AND c.state = :state")
    Optional<ProjectComment> findByIdAndStateWithPessimisticLock(Integer commentIdx, State state);

    List<ProjectComment> findAllByProjectAndStateOrderByIdAsc(Project project, State state);

}
