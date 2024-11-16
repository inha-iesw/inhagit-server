package inha.git.banner.domain.repository;


import inha.git.banner.domain.Banner;
import inha.git.common.BaseEntity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


/**
 * BannerJpaRepository는 Banner 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface BannerJpaRepository extends JpaRepository<Banner, Integer> {

    List<Banner> findAllByState(State state);

}
