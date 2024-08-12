package inha.git.question.domain.repository;


import inha.git.question.domain.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * QuestionCommentJpaRepository는 QuestionComment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionCommentJpaRepository extends JpaRepository<QuestionComment, Integer> {


}
