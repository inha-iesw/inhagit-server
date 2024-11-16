package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.QuestionCommentLike;
import inha.git.mapping.domain.QuestionReplyCommentLike;
import inha.git.mapping.domain.id.QuestionCommentLikeId;
import inha.git.mapping.domain.id.QuestionReplyCommentLikeId;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.QuestionReplyComment;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * QuestionCommentLikeJpaRepository는 QuestionCommentLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionCommentLikeJpaRepository extends JpaRepository<QuestionCommentLike, QuestionCommentLikeId> {

    boolean existsByUserAndQuestionComment(User user, QuestionComment questionComment);

    void deleteByUserAndQuestionComment(User user, QuestionComment questionComment);
}
