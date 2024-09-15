package inha.git.user.domain.repository;


import inha.git.user.domain.EmailDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * EmailDomainJpaRepository는 EmailDomain 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface EmailDomainJpaRepository extends JpaRepository<EmailDomain, Integer> {


    Optional<EmailDomain> findByUserTypeAndEmailDomain(Integer userType, String domain);
}
