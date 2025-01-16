package inha.git.notice.domain.repository;

import inha.git.common.BaseEntity.State;
import inha.git.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * BannerJpaRepository는 Banner 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface NoticeJpaRepository extends JpaRepository<Notice, Integer> {
    Optional<Notice> findByIdAndState(Integer id, State state);
}
