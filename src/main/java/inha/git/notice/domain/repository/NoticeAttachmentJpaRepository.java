package inha.git.notice.domain.repository;

import inha.git.notice.domain.NoticeAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * NoticeAttachmentJpaRepository는 NoticeAttachment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface NoticeAttachmentJpaRepository extends JpaRepository<NoticeAttachment, Integer> {
}
