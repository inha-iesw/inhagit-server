package inha.git.question.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.field.domain.Field;
import inha.git.question.domain.Question;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * QuestionJpaRepository는 Question 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface QuestionJpaRepository extends JpaRepository<Question, Integer> {
    Optional<Question> findByIdAndState(Integer questionIdx, State state);
    long countByUserAndSemesterAndQuestionFields_FieldAndState(User user, Semester semester, Field field, State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT q FROM Question q WHERE q.id = :questionIdx AND q.state = :state")
    Optional<Question> findByIdAndStateWithPessimisticLock(Integer questionIdx, State state);

    @Query("SELECT DISTINCT q, u.userNumber FROM Question q " +
            "JOIN q.user u " +
            "JOIN u.userDepartments ud " +
            "JOIN ud.department d " +
            "JOIN d.college c " +
            "JOIN q.semester s " +
            "JOIN q.category cat " +
            "JOIN q.questionFields qf " +
            "JOIN qf.field f " +
            "WHERE q.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY u.userNumber ASC")
    List<Question> findAllQuestions(
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);

    @Query("SELECT DISTINCT q, u.userNumber FROM Question q " +
            "JOIN q.user u " +
            "JOIN u.userDepartments ud " +
            "JOIN ud.department d " +
            "JOIN d.college c " +
            "JOIN q.semester s " +
            "JOIN q.category cat " +
            "JOIN q.questionFields qf " +
            "JOIN qf.field f " +
            "WHERE c.id = :collegeId " +
            "AND q.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY u.userNumber ASC")
    List<Question> findAllQuestionsByCollege(
            @Param("collegeId") Integer collegeId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);

    @Query("SELECT DISTINCT q, u.userNumber FROM Question q " +
            "JOIN q.user u " +
            "JOIN u.userDepartments ud " +
            "JOIN ud.department d " +
            "JOIN d.college c " +
            "JOIN q.semester s " +
            "JOIN q.category cat " +
            "JOIN q.questionFields qf " +
            "JOIN qf.field f " +
            "WHERE d.id = :departmentId " +
            "AND q.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY u.userNumber ASC")
    List<Question> findAllQuestionsByDepartment(
            @Param("departmentId") Integer departmentId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);

    @Query("SELECT DISTINCT q, u.userNumber FROM Question q " +
            "JOIN q.user u " +
            "JOIN u.userDepartments ud " +
            "JOIN ud.department d " +
            "JOIN d.college c " +
            "JOIN q.semester s " +
            "JOIN q.category cat " +
            "JOIN q.questionFields qf " +
            "JOIN qf.field f " +
            "WHERE u.id = :userId " +
            "AND q.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY u.userNumber ASC")
    List<Question> findAllQuestionsByUser(
            @Param("userId") Integer userId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);
}
