package inha.git.category.domain.repository;

import inha.git.category.domain.Category;
import inha.git.common.BaseEntity.State;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * CategoryJpaRepository는 Category 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface CategoryJpaRepository extends JpaRepository<Category, Integer> {
    boolean existsByNameAndState(String name, State state);
    Optional<Category> findByIdAndState(Integer semesterIdx, State state);
    List<Category> findAllByState(State state, Sort sort);
    List<Category> findAllByState(State state);
    Optional<Category> findByNameAndState(String name, State state);
}
