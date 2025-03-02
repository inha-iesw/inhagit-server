package inha.git.question.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;
import java.util.Optional;

/**
 * QuestionCommentJpaRepository는 QuestionComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionCommentJpaRepository extends JpaRepository<QuestionComment, Integer> {
    Optional<QuestionComment> findByIdAndState(Integer commentIdx, State state);
    List<QuestionComment> findAllByQuestionAndStateOrderByIdAsc(Question question, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT c FROM QuestionComment c WHERE c.id = :commentIdx AND c.state = :state")
    Optional<QuestionComment> findByIdAndStateWithPessimisticLock(Integer commentIdx, State state);
}
