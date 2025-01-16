package inha.git.project.domain.repository;


import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProjectField;
import inha.git.project.domain.Project;
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

import static inha.git.common.BaseEntity.*;


/**
 * ProjectJpaRepository는 Project 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Integer> {
    Optional<Project> findByIdAndState(Integer projectIdx, State state);
    long countByUserAndSemesterAndProjectFields_FieldAndState(User user, Semester semester, Field field, State state);
    List<Project> findAllByStateOrderById(State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.state = :state")
    Optional<Project> findByIdAndStateWithPessimisticLock(@Param("id") Integer id, @Param("state") State state);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN FETCH p.user u " +
            "JOIN FETCH u.userDepartments ud " +
            "JOIN FETCH ud.department d " +
            "JOIN FETCH d.college c " +
            "JOIN FETCH p.semester s " +
            "JOIN FETCH p.category cat " +
            "WHERE p.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY c.id ASC, d.id ASC, u.userNumber ASC")
    List<Project> findAllByState(@Param("semesterId") Integer semesterId,
                                 @Param("state") State state);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN FETCH p.user u " +
            "JOIN FETCH u.userDepartments ud " +
            "JOIN FETCH ud.department d " +
            "JOIN FETCH d.college c " +
            "JOIN FETCH p.semester s " +
            "JOIN FETCH p.category cat " +
            "WHERE c.id = :collegeId " +
            "AND p.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY c.id ASC, d.id ASC, u.userNumber ASC")
    List<Project> findAllByUserCollegeIdAndState(
            @Param("collegeId") Integer collegeId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN FETCH p.user u " +
            "JOIN FETCH u.userDepartments ud " +
            "JOIN FETCH ud.department d " +
            "JOIN FETCH d.college c " +
            "JOIN FETCH p.semester s " +
            "JOIN FETCH p.category cat " +
            "WHERE d.id = :departmentId " +
            "AND p.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY c.id ASC, d.id ASC, u.userNumber ASC")
    List<Project> findAllByUserDepartmentIdAndState(
            @Param("departmentId") Integer departmentId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);

    @Query("SELECT DISTINCT p FROM Project p " +
            "JOIN FETCH p.user u " +
            "JOIN FETCH u.userDepartments ud " +
            "JOIN FETCH ud.department d " +
            "JOIN FETCH d.college c " +
            "JOIN FETCH p.semester s " +
            "JOIN FETCH p.category cat " +
            "WHERE u.id = :userId " +
            "AND p.state = :state " +
            "AND (:semesterId IS NULL OR s.id = :semesterId) " +
            "ORDER BY p.createdAt DESC")
    List<Project> findAllByUserIdAndState(
            @Param("userId") Integer userId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state);

    @Query("SELECT DISTINCT pf FROM ProjectField pf " +
            "JOIN FETCH pf.field " +
            "WHERE pf.project.id IN :projectIds")
    List<ProjectField> findProjectFieldsByProjectIds(@Param("projectIds") List<Integer> projectIds);
}
