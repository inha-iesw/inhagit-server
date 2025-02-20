package inha.git.report.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.report.api.controller.dto.response.ReportReasonResponse;
import inha.git.report.api.controller.dto.response.ReportTypeResponse;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import inha.git.report.domain.Report;
import inha.git.user.domain.QUser;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.report.domain.QReport.report;
import static inha.git.report.domain.QReportReason.reportReason;
import static inha.git.report.domain.QReportType.reportType;

@Repository
@RequiredArgsConstructor
public class ReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<SearchReportResponse> getUserReports(Integer reporterId, Pageable pageable) {
        BooleanExpression condition = report.reporterId.eq(reporterId);

        QUser reporter = new QUser("reporter");
        QUser reported = new QUser("reported");

        JPAQuery<Report> query = queryFactory
                .select(report)
                .from(report)
                .leftJoin(report.reportType, reportType)
                .leftJoin(report.reportReason, reportReason)
                .leftJoin(reporter).on(report.reporterId.eq(reporter.id))
                .leftJoin(reported).on(report.reportedUserId.eq(reported.id))
                .where(condition)
                .orderBy(report.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Report> reports = query.fetch();
        long total = query.fetchCount();

        List<Integer> reportedIds = reports.stream()
                .map(Report::getReportedUserId)
                .distinct()
                .toList();

        User reporterUser = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.eq(reporterId))
                .fetchOne();

        Map<Integer, User> reportedMap = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.in(reportedIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<SearchReportResponse> content = reports.stream()
                .map(r -> {
                    User reportedUser = reportedMap.get(r.getReportedUserId());

                    return new SearchReportResponse(
                            r.getId(),
                            new ReportTypeResponse(
                                    r.getReportType().getId(),
                                    r.getReportType().getName()
                            ),
                            new ReportReasonResponse(
                                    r.getReportReason().getId(),
                                    r.getReportReason().getName()
                            ),
                            new SearchUserResponse(
                                    reporterUser.getId(),
                                    reporterUser.getName(),
                                    mapRoleToPosition(reporterUser.getRole())
                            ),
                            new SearchUserResponse(
                                    reportedUser.getId(),
                                    reportedUser.getName(),
                                    mapRoleToPosition(reportedUser.getRole())
                            ),
                            r.getDescription(),
                            r.getState(),
                            r.getCreatedAt(),
                            r.getDeletedAt()
                    );
                })
                .toList();
        return new PageImpl<>(content, pageable, total);
    }
}
