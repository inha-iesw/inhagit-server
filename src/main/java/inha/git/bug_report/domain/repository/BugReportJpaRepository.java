package inha.git.bug_report.domain.repository;


import inha.git.bug_report.domain.BugReport;
import inha.git.common.BaseEntity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * BugReportJpaRepository는 BugReport 엔티티에 대한 데이터 액세스 기능을 제공.
 */
public interface BugReportJpaRepository extends JpaRepository<BugReport, Integer> {

    Optional<BugReport> findByIdAndState(Integer bugReportId, State state);
}
