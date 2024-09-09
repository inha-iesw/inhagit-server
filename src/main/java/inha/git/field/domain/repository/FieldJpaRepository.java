package inha.git.field.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.field.domain.Field;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.*;


/**
 * FieldJpaRepository는 Field 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface FieldJpaRepository extends JpaRepository<Field, Integer> {

    Optional<Field> findByIdAndState(Integer id, State state);
    boolean existsByNameAndState(String name, State state);

    List<Field> findAllByState(State state);

    Optional<Field> findByNameAndState(String name, State state);
}
