package inha.git.banner.domain.repository;


import inha.git.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * BannerJpaRepository는 Banner 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface BannerJpaRepository extends JpaRepository<Banner, Integer> {


}
