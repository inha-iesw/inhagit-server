package inha.git.semester.domain.repository;


import inha.git.common.BaseEntity.State;
import inha.git.semester.domain.Semester;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * SemesterJpaRepository는 Semester 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface SemesterJpaRepository extends JpaRepository<Semester, Integer> {

    boolean existsByNameAndState(String name, State state);
}
