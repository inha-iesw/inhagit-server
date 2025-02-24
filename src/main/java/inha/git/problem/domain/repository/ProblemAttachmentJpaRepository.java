package inha.git.problem.domain.repository;

import inha.git.problem.domain.ProblemAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProblemAttachmentJpaRepository는 ProblemAttachment 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ProblemAttachmentJpaRepository extends JpaRepository<ProblemAttachment, Integer> {
}
