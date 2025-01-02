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
        // 기본 조건 설정 - ACTIVE 상태이고 특정 reporterId를 가진 신고만 조회
        BooleanExpression condition = report.reporterId.eq(reporterId);

        QUser reporter = new QUser("reporter");
        QUser reported = new QUser("reported");

        // 메인 쿼리 구성
        JPAQuery<Report> query = queryFactory
                .select(report)
                .from(report)
                .leftJoin(report.reportType, reportType)
                .leftJoin(report.reportReason, reportReason)
                // reporter와 reported user를 각각 조인
                .leftJoin(reporter).on(report.reporterId.eq(reporter.id))
                .leftJoin(reported).on(report.reportedUserId.eq(reported.id))
                .where(condition)
                .orderBy(report.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 결과 및 총 개수 조회
        List<Report> reports = query.fetch();
        long total = query.fetchCount();

        // User 정보를 한 번에 조회
        List<Integer> reportedIds = reports.stream()
                .map(Report::getReportedUserId)
                .distinct()
                .toList();

        // Reporter 정보는 이미 알고 있으므로 한 번만 조회
        User reporterUser = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.eq(reporterId))
                .fetchOne();

        // Reported User 정보 조회
        Map<Integer, User> reportedMap = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.in(reportedIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // SearchReportResponse로 변환
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
