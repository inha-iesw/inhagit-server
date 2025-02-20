package inha.git.project.domain.repository;

import inha.git.field.domain.Field;
import inha.git.project.api.controller.dto.response.PatentResponses;
import inha.git.project.domain.ProjectPatent;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State;

/**
 * ProjectCommentJpaRepository는 Project 특허 엔티티에 대한 데이터 액세스 기능을 제공.
 */
@Repository
public interface ProjectPatentJpaRepository extends JpaRepository<ProjectPatent, Integer> {
    Optional<ProjectPatent> findByIdAndState(Integer projectPatentId, State state);

    Optional<ProjectPatent> findByApplicationNumberAndState(String applicationNumber, State state);

    long countByProject_UserAndProject_SemesterAndProject_ProjectFields_FieldAndProject_State(
            User user,
            Semester semester,
            Field field,
            State state
    );

    // 기본 조회용 쿼리 (projectPatentInventors 제외)
    @Query("SELECT DISTINCT pp FROM ProjectPatent pp " +
            "JOIN FETCH pp.project p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH u.userDepartments ud " +
            "LEFT JOIN FETCH ud.department d " +
            "LEFT JOIN FETCH d.college c " +
            "WHERE pp.acceptAt IS NOT NULL " +
            "AND p.state = :state " +
            "AND (:semesterId IS NULL OR p.semester.id = :semesterId) " +
            "ORDER BY pp.patentType ASC, u.userNumber ASC")
    List<ProjectPatent> findAllAcceptedPatents(
            @Param("semesterId") Integer semesterId,
            @Param("state") State state
    );

    // inventor 조회용 쿼리
    @Query("SELECT DISTINCT pp FROM ProjectPatent pp " +
            "JOIN FETCH pp.projectPatentInventors " +
            "WHERE pp IN :patents")
    List<ProjectPatent> findPatentsWithInventors(
            @Param("patents") List<ProjectPatent> patents
    );

    @Query("SELECT DISTINCT pp FROM ProjectPatent pp " +
            "JOIN FETCH pp.project p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH u.userDepartments ud " +
            "LEFT JOIN FETCH ud.department d " +
            "LEFT JOIN FETCH d.college c " +
            "WHERE pp.acceptAt IS NOT NULL " +
            "AND p.state = :state " +
            "AND c.id = :collegeId " +
            "AND (:semesterId IS NULL OR p.semester.id = :semesterId) " +
            "ORDER BY pp.patentType ASC, u.userNumber ASC")
    List<ProjectPatent> findAllAcceptedPatentsByCollege(
            @Param("collegeId") Integer collegeId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state
    );

    @Query("SELECT DISTINCT pp FROM ProjectPatent pp " +
            "JOIN FETCH pp.project p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH u.userDepartments ud " +
            "LEFT JOIN FETCH ud.department d " +
            "LEFT JOIN FETCH d.college c " +
            "WHERE pp.acceptAt IS NOT NULL " +
            "AND p.state = :state " +
            "AND d.id = :departmentId " +
            "AND (:semesterId IS NULL OR p.semester.id = :semesterId) " +
            "ORDER BY pp.patentType ASC, u.userNumber ASC")
    List<ProjectPatent> findAllAcceptedPatentsByDepartment(
            @Param("departmentId") Integer departmentId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state
    );

    @Query("SELECT DISTINCT pp FROM ProjectPatent pp " +
            "JOIN FETCH pp.project p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH u.userDepartments ud " +
            "LEFT JOIN FETCH ud.department d " +
            "LEFT JOIN FETCH d.college c " +
            "WHERE pp.acceptAt IS NOT NULL " +
            "AND p.state = :state " +
            "AND u.id = :userId " +
            "AND (:semesterId IS NULL OR p.semester.id = :semesterId) " +
            "ORDER BY pp.patentType ASC, u.userNumber ASC")
    List<ProjectPatent> findAllAcceptedPatentsByUser(
            @Param("userId") Integer userId,
            @Param("semesterId") Integer semesterId,
            @Param("state") State state
    );

    // 특정 특허 조회 시 inventors 포함하여 조회
    @Query("SELECT pp FROM ProjectPatent pp " +
            "JOIN FETCH pp.project p " +
            "JOIN FETCH p.user u " +
            "LEFT JOIN FETCH pp.projectPatentInventors " +
            "WHERE pp.id = :patentId " +
            "AND pp.state = :state")
    Optional<ProjectPatent> findByIdWithInventors(
            @Param("patentId") Integer patentId,
            @Param("state") State state
    );

    Page<ProjectPatent> findByAcceptAtIsNotNullAndStateOrderByCreatedAtDesc(State state, Pageable pageable);
}
