package inha.git.user.domain.repository;


import inha.git.user.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * ProfessorJpaRepository는 Professor 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProfessorJpaRepository extends JpaRepository<Professor, Integer> {


}
