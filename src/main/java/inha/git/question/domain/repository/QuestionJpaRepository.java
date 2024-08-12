package inha.git.question.domain.repository;


import inha.git.question.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * QuestionJpaRepository는 Question 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionJpaRepository extends JpaRepository<Question, Integer> {


}
