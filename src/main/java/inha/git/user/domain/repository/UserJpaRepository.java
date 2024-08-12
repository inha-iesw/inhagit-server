package inha.git.user.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserJpaRepository는 User 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface UserJpaRepository extends JpaRepository<User, Integer> {

    /**
     * 사용자명과 상태를 기반으로 사용자를 찾음.
     *
     * @param email 찾으려는 사용자의 email
     * @param state 찾으려는 사용자의 상태 (ACTIVE 또는 INACTIVE)
     * @return 조건에 맞는 사용자를 포함하는 Optional 객체
     */
    Optional<User> findByEmailAndState(String email, BaseEntity.State state);

    boolean existsByEmail(String email);
}
