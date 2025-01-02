package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.QuestionLike;
import inha.git.mapping.domain.id.QuestionLikeId;
import inha.git.question.domain.Question;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * QuestionLikeJpaRepository는 QuestionLike 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionLikeJpaRepository extends JpaRepository<QuestionLike, QuestionLikeId> {

    boolean existsByUserAndQuestion(User user, Question question);

    void deleteByUserAndQuestion(User user, Question question);
}
