package inha.git.banner.domain.repository;


import inha.git.common.BaseEntity;
import inha.git.user.domain.User;
import org.springframework.boot.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * BannerJpaRepository는 Banner 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface BannerJpaRepository extends JpaRepository<Banner, Integer> {


}
