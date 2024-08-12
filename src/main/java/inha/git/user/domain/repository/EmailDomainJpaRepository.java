package inha.git.user.domain.repository;


import inha.git.user.domain.EmailDomain;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * EmailDomainJpaRepository는 EmailDomain 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface EmailDomainJpaRepository extends JpaRepository<EmailDomain, Integer> {


}
