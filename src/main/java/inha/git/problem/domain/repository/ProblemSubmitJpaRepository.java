package inha.git.problem.domain.repository;

import inha.git.problem.domain.ProblemRequest;
import inha.git.problem.domain.ProblemSubmit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * ProblemSubmitJpaRepository는 Problem 제출 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemSubmitJpaRepository extends JpaRepository<ProblemSubmit, Integer> {

    Page<ProblemSubmit> findByProblemRequest_Problem_Id(Integer problemIdx, Pageable pageable);

    @Query("SELECT ps.projectId FROM ProblemSubmit ps WHERE ps.problemRequest.id = :problemRequestId")
    Optional<Integer> findProjectIdByProblemRequestId(@Param("problemRequestId") Integer problemRequestId);

    boolean existsByProblemRequest(ProblemRequest problemRequest);
}
