package inha.git.team.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.team.domain.TeamPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * TeamPostJpaRepository는 TeamPost 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface TeamPostJpaRepository extends JpaRepository<TeamPost, Integer> {


    Optional<TeamPost> findByIdAndState(Integer postIdx, BaseEntity.State state);
}
