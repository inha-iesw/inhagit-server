package inha.git.field.domain.repository;


import inha.git.field.domain.Field;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * FieldJpaRepository는 Field 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface FieldJpaRepository extends JpaRepository<Field, Integer> {


}
