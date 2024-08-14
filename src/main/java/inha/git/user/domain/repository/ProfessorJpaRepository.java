package inha.git.user.domain.repository;


import inha.git.user.domain.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * ProfessorJpaRepository는 Professor 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProfessorJpaRepository extends JpaRepository<Professor, Integer> {

    //유저 아이디로 찾기
    Optional<Professor> findByUserId(Integer userId);


}
