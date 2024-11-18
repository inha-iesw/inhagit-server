package inha.git.admin.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.request.SearchReportCond;
import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.common.BaseEntity.State;
import inha.git.mapping.domain.QUserDepartment;
import inha.git.report.api.controller.dto.response.ReportReasonResponse;
import inha.git.report.api.controller.dto.response.ReportTypeResponse;
import inha.git.report.api.controller.dto.response.SearchReportResponse;
import inha.git.report.domain.Report;
import inha.git.user.domain.QCompany;
import inha.git.user.domain.QProfessor;
import inha.git.user.domain.QUser;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.report.domain.QReport.report;
import static inha.git.report.domain.QReportReason.reportReason;
import static inha.git.report.domain.QReportType.reportType;

@Repository
@RequiredArgsConstructor
public class AdminQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 사용자 목록
     */
    public Page<SearchUserResponse> searchUsers(String search, Pageable pageable) {
        QUser user = QUser.user;

        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .where(nameLike(search),user.state.eq(State.ACTIVE))
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        List<SearchUserResponse> content = query.fetch().stream()
                .map(u -> new SearchUserResponse(u))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 학생 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 학생 목록
     */
    public Page<SearchStudentResponse> searchStudents(String search, Pageable pageable) {
        QUser user = QUser.user;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(user.userDepartments, userDepartment)
                .where(
                        user.role.eq(Role.USER).or(user.role.eq(Role.ASSISTANT)),
                        nameLike(search),
                        user.state.eq(State.ACTIVE)
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SearchStudentResponse> content = query.fetch().stream()
                .map(u -> new SearchStudentResponse(u))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 교수 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 교수 목록
     */
    public Page<SearchProfessorResponse> searchProfessors(String search, Pageable pageable) {
        QUser user = QUser.user;
        QProfessor professor = QProfessor.professor;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(professor).on(user.id.eq(professor.user.id))
                .leftJoin(user.userDepartments, userDepartment)
                .where(
                        user.role.eq(Role.PROFESSOR),
                        nameLike(search),
                        user.state.eq(State.ACTIVE)
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SearchProfessorResponse> content = query.fetch().stream()
                .map(u -> new SearchProfessorResponse(
                        u,
                        u.getProfessor()
                ))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }
    /**
     * 회사 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 회사 목록
     */
    public Page<SearchCompanyResponse> searchCompanies(String search, Pageable pageable) {
        QUser user = QUser.user;
        QCompany company = QCompany.company;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(user.userDepartments, userDepartment)
                .leftJoin(company).on(user.id.eq(company.user.id))
                .where(
                        user.role.eq(Role.COMPANY),
                        nameLike(search),
                        user.state.eq(State.ACTIVE)
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SearchCompanyResponse> content = query.fetch().stream()
                .map(u -> new SearchCompanyResponse(u, u.getCompany()))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 이름 검색
     *
     * @param search 검색어
     * @return 검색 조건
     */
    private BooleanExpression nameLike(String search) {
        return StringUtils.hasText(search) ? QUser.user.name.contains(search) : null;
    }


    /**
     * 신고 검색
     *
     * @param searchReportCond 신고 검색 조건
     * @param pageable         페이지 정보
     * @return 신고 목록
     */
    public Page<SearchReportResponse> searchReports(SearchReportCond searchReportCond, Pageable pageable) {
        // 기본 조건 설정
        BooleanExpression condition = report.isNotNull();
        QUser reporter = new QUser("reporter");
        QUser reported = new QUser("reported");

        // 동적 조건 추가
        if (searchReportCond.reportTypeIdx() != null) {
            condition = condition.and(report.reportType.id.eq(searchReportCond.reportTypeIdx()));
        }

        if (searchReportCond.reportReasonIdx() != null) {
            condition = condition.and(report.reportReason.id.eq(searchReportCond.reportReasonIdx()));
        }

        if (searchReportCond.reporterId() != null) {
            condition = condition.and(report.reporterId.eq(searchReportCond.reporterId()));
        }

        if (searchReportCond.reportedUserId() != null) {
            condition = condition.and(report.reportedUserId.eq(searchReportCond.reportedUserId()));
        }

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
        List<Integer> reporterIds = reports.stream()
                .map(Report::getReporterId)
                .distinct()
                .toList();
        List<Integer> reportedIds = reports.stream()
                .map(Report::getReportedUserId)
                .distinct()
                .toList();

        // User 정보 조회
        Map<Integer, User> reporterMap = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.in(reporterIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        Map<Integer, User> reportedMap = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.in(reportedIds))
                .fetch()
                .stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // SearchReportResponse로 변환
        List<SearchReportResponse> content = reports.stream()
                .map(r -> {
                    User reporterUser = reporterMap.get(r.getReporterId());
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
                            new inha.git.project.api.controller.dto.response.SearchUserResponse(
                                    reporterUser.getId(),
                                    reporterUser.getName(),
                                    mapRoleToPosition(reporterUser.getRole())
                            ),
                            new inha.git.project.api.controller.dto.response.SearchUserResponse(
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
