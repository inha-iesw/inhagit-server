package inha.git.report.domain.repository;

import inha.git.report.domain.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ReportReasonJpaRepository는 ReportReason 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ReportReasonJpaRepository extends JpaRepository<ReportReason, Integer> {
    Optional<ReportReason> findByName(String reportReason);
}
