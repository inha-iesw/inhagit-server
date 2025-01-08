package inha.git.project.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.field.domain.Field;
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
import java.util.Optional;


/**
 * ProjectJpaRepository는 Project 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Integer> {


    Optional<Project> findByIdAndState(Integer projectIdx, BaseEntity.State state);

    long countByUserAndSemesterAndProjectFields_FieldAndState(User user, Semester semester, Field field, BaseEntity.State state);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.state = :state")
    Optional<Project> findByIdAndStateWithPessimisticLock(@Param("id") Integer id, @Param("state") BaseEntity.State state);
}
