package inha.git.report.domain.repository;

import inha.git.report.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ReportTypeJpaRepository는 ReportType 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ReportTypeJpaRepository extends JpaRepository<ReportType, Integer> {
    Optional<ReportType> findByName(String reportType);
}
