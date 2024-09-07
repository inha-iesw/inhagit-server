package inha.git.college.domain.repository;


import inha.git.college.domain.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * CollegeJpaRepository는 College 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface CollegeJpaRepository extends JpaRepository<College, Integer> {

    Optional<College> findByName(String name);

}
