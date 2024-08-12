package inha.git.question.domain.repository;


import inha.git.question.domain.QuestionReplyComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * QuestionReplyCommentJpaRepository는 QuestionReplyComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionReplyCommentJpaRepository extends JpaRepository<QuestionReplyComment, Integer> {


}
