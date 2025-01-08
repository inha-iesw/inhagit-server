package inha.git.question.domain.repository;


import inha.git.common.BaseEntity.State;
import inha.git.question.domain.Question;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;


/**
 * QuestionJpaRepository는 Question 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionJpaRepository extends JpaRepository<Question, Integer> {
    Optional<Question> findByIdAndState(Integer questionIdx, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT q FROM Question q WHERE q.id = :questionIdx AND q.state = :state")
    Optional<Question> findByIdAndStateWithPessimisticLock(Integer questionIdx, State state);
}
