package inha.git.user.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * UserJpaRepository는 User 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface UserJpaRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmailAndState(String email, State state);
    boolean existsByEmailAndState(String email, State state);
    boolean existsByUserNumberAndState(String userNumber, State state);
    Optional<User> findByIdAndState(Integer id, State state);
    Optional<User> findByEmail(String mail);
    Optional<User> findByUserNumberAndName(String userNumber, String name);
    List<User> findAllByState(State state);
}
