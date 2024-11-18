package inha.git.image.domain.repository;

import inha.git.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ImageJpaRepository는 Image 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ImageJpaRepository extends JpaRepository<Image, Integer> {
}
