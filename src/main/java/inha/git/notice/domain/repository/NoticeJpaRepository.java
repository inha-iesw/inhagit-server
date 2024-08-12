package inha.git.notice.domain.repository;


import inha.git.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * BannerJpaRepository는 Banner 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface NoticeJpaRepository extends JpaRepository<Notice, Integer> {


}
