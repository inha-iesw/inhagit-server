package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.id.QuestionFieldId;
import inha.git.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProjectFieldJpaRepository는 ProjectField엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionFieldJpaRepository extends JpaRepository<QuestionField, QuestionFieldId> {


    void deleteByQuestion(Question question);
}
