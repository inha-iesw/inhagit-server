package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.ProblemField;
import inha.git.mapping.domain.id.ProblemFieldId;
import inha.git.problem.domain.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


/**
 * ProblemFieldJpaRepository는 ProblemField엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemFieldJpaRepository extends JpaRepository<ProblemField, ProblemFieldId> {


    @Modifying
    @Query("DELETE FROM ProblemField pf WHERE pf.problem = :problem")
    void deleteByProblem(@Param("problem") Problem problem);
    List<ProblemField> findByProblem(Problem problem);

    Optional<ProblemField> findByProblemAndFieldId(Problem problem, Integer fieldId);

    void deleteByProblemAndFieldId(Problem problem, Integer id);
}
