package inha.git.department.domain.repository;


import inha.git.college.domain.College;
import inha.git.common.BaseEntity;
import inha.git.common.BaseEntity.State;
import inha.git.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * DepartmentJpaRepository는 Department 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface DepartmentJpaRepository extends JpaRepository<Department, Integer> {



    List<Department> findAllByState(State state);


    Optional<Department> findByIdAndState(Integer id, State state);

    boolean existsByNameAndState(String name, State state);

    Optional<Department> findByName(String name);

    List<Department> findAllByCollegeAndState(College college, State state);
}
