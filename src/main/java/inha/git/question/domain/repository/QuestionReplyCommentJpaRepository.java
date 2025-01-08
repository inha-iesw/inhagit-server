package inha.git.question.domain.repository;


import inha.git.common.BaseEntity.State;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.QuestionReplyComment;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;


/**
 * QuestionReplyCommentJpaRepository는 QuestionReplyComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionReplyCommentJpaRepository extends JpaRepository<QuestionReplyComment, Integer> {


    Optional<QuestionReplyComment> findByIdAndState(Integer commentIdx, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT c FROM QuestionReplyComment c WHERE c.id = :commentIdx AND c.state = :state")
    Optional<QuestionReplyComment> findByIdAndStateWithPessimisticLock(Integer commentIdx, State state);

    boolean existsByQuestionCommentAndState(QuestionComment questionComment, State state);
}
