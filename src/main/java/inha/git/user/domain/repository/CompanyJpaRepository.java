package inha.git.user.domain.repository;


import inha.git.user.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CompanyJpaRepository는 Company 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface CompanyJpaRepository extends JpaRepository<Company, Integer> {


}
