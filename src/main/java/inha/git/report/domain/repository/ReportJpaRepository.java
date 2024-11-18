package inha.git.report.domain.repository;

import inha.git.report.domain.Report;
import inha.git.report.domain.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ReportJpaRepository는 Report 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface ReportJpaRepository extends JpaRepository<Report, Integer> {

    Boolean existsByReporterIdAndReportedIdAndReportType(Integer reporterId, Integer reportedId, ReportType reportType);
}
