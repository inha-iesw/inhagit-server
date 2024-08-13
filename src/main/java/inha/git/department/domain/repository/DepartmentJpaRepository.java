package inha.git.department.domain.repository;


import inha.git.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * DepartmentJpaRepository는 Department 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface DepartmentJpaRepository extends JpaRepository<Department, Integer> {


    boolean existsByName(String name);
}
