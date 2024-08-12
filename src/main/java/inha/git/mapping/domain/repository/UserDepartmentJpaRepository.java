package inha.git.mapping.domain.repository;


import inha.git.mapping.domain.UserDepartment;
import inha.git.mapping.domain.id.UserDepartmentId;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * UserDepartmentJpaRepository는 UserDepartment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface UserDepartmentJpaRepository extends JpaRepository<UserDepartment, UserDepartmentId> {


}
