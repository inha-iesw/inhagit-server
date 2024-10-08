package inha.git.question.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.common.BaseEntity.State;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


/**
 * QuestionCommentJpaRepository는 QuestionComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionCommentJpaRepository extends JpaRepository<QuestionComment, Integer> {


    Optional<QuestionComment> findByIdAndState(Integer commentIdx, State state);

    List<QuestionComment> findAllByQuestionAndStateOrderByIdAsc(Question question, State state);


}
