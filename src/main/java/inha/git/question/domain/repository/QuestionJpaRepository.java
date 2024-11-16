package inha.git.question.domain.repository;


import inha.git.common.BaseEntity.State;
import inha.git.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * QuestionJpaRepository는 Question 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionJpaRepository extends JpaRepository<Question, Integer> {
    Optional<Question> findByIdAndState(Integer questionIdx, State state);
}
