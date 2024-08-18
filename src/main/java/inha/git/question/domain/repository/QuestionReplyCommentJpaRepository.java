package inha.git.question.domain.repository;


import inha.git.common.BaseEntity.State;
import inha.git.question.domain.QuestionReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * QuestionReplyCommentJpaRepository는 QuestionReplyComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionReplyCommentJpaRepository extends JpaRepository<QuestionReplyComment, Integer> {


    Optional<QuestionReplyComment> findByIdAndState(Integer commentIdx, State state);
}
