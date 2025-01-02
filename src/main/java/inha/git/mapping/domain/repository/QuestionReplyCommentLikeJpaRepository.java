package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.QuestionReplyCommentLike;
import inha.git.mapping.domain.id.QuestionReplyCommentLikeId;
import inha.git.question.domain.QuestionReplyComment;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * QuestionReplyCommentLikeJpaRepository는 QuestionReplyCommentLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionReplyCommentLikeJpaRepository extends JpaRepository<QuestionReplyCommentLike, QuestionReplyCommentLikeId> {

    boolean existsByUserAndQuestionReplyComment(User user, QuestionReplyComment questionReplyComment);

    void deleteByUserAndQuestionReplyComment(User user, QuestionReplyComment questionReplyComment);
}
