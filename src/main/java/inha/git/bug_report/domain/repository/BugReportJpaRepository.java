package inha.git.bug_report.domain.repository;


import inha.git.bug_report.domain.BugReport;
import org.springframework.data.jpa.repository.JpaRepository;



/**
 * BugReportJpaRepository는 BugReport 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface BugReportJpaRepository extends JpaRepository<BugReport, Integer> {


}
