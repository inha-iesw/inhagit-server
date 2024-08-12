package inha.git.assistant.domain.repository;


import inha.git.assistant.domain.Assistant;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * AssistantJpaRepository는 Assistant 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface AssistantJpaRepository extends JpaRepository<Assistant, Integer> {


}
