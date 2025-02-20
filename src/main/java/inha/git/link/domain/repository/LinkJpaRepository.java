package inha.git.link.domain.repository;

import inha.git.link.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * BannerJpaRepository는 Banner 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface LinkJpaRepository extends JpaRepository<Link, Integer> {
}
