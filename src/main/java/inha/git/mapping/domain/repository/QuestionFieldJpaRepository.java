package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.id.QuestionFieldId;
import inha.git.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


/**
 * ProjectFieldJpaRepository는 ProjectField엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionFieldJpaRepository extends JpaRepository<QuestionField, QuestionFieldId> {

    @Modifying
    @Query("DELETE FROM QuestionField qf WHERE qf.question = :question")
    void deleteByQuestion(@Param("question") Question question);

    List<QuestionField> findByQuestion(Question question);

    Optional<QuestionField> findByQuestionAndFieldId(Question question, Integer fieldId);
}
