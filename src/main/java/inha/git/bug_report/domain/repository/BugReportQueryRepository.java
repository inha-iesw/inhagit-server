package inha.git.bug_report.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.bug_report.api.controller.dto.request.SearchBugReportCond;
import inha.git.bug_report.api.controller.dto.response.SearchBugReportsResponse;
import inha.git.bug_report.domain.BugReport;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inha.git.bug_report.domain.QBugReport.bugReport;
import static inha.git.common.Constant.mapRoleToPosition;

@Repository
@RequiredArgsConstructor
public class BugReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 사용자의 버그 리포트 목록 조회
     *
     * @param userId 사용자 ID
     * @param searchBugReportCond 버그 리포트 검색 조건
     * @param pageable 페이징 정보
     * @return 버그 리포트 목록
     */
    public Page<SearchBugReportsResponse> getUserBugReports(Integer userId, SearchBugReportCond searchBugReportCond, Pageable pageable) {
        // 기본 조건: 특정 사용자의 버그 리포트
        BooleanExpression condition = bugReport.user.id.eq(userId);

        // 제목 검색 조건
        if (searchBugReportCond.title() != null && !searchBugReportCond.title().isEmpty()) {
            condition = condition.and(bugReport.title.containsIgnoreCase(searchBugReportCond.title()));
        }

        // 버그 상태 검색 조건
        if (searchBugReportCond.bugStatus() != null) {
            condition = condition.and(bugReport.bugStatus.eq(searchBugReportCond.bugStatus()));
        }

        // 버그 리포트 목록 조회 쿼리
        JPAQuery<BugReport> query = queryFactory
                .select(bugReport)
                .from(bugReport)
                .leftJoin(bugReport.user)
                .where(condition)
                .orderBy(bugReport.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 결과 리스트 및 총 개수 가져오기
        List<BugReport> bugReports = query.fetch();
        long total = query.fetchCount();

        // SearchBugReportsResponse 변환
        List<SearchBugReportsResponse> content = bugReports.stream()
                .map(report -> new SearchBugReportsResponse(
                        report.getId(),
                        report.getTitle(),
                        report.getCreatedAt(),
                        report.getBugStatus(),
                        new SearchUserResponse(
                                report.getUser().getId(),
                                report.getUser().getName(),
                                mapRoleToPosition(report.getUser().getRole()
                        )
                ))
                ).toList();
        return new PageImpl<>(content, pageable, total);
    }
}