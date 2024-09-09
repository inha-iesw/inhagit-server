package inha.git.statistics.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.statistics.api.controller.dto.request.ProjectSearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static inha.git.statistics.domain.QCollegeStatistics.collegeStatistics;
import static inha.git.statistics.domain.QDepartmentStatistics.departmentStatistics;
import static inha.git.statistics.domain.QUserCountStatistics.userCountStatistics;

/**
 * StatisticsQueryRepository는 통계 쿼리를 처리하는 레포지토리.
 */
@Repository
@RequiredArgsConstructor
public class ProjectStatisticsQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 프로젝트 통계 조회
     *
     * @param searchCond 프로젝트 검색 조건
     * @return ProjectStatisticsResponse
     */
    public ProjectStatisticsResponse getProjectStatistics(ProjectSearchCond searchCond) {
        // 전체 프로젝트 수
        Integer totalProjectCount = getTotalProjectCount(searchCond);

        // 로컬 프로젝트 수
        Integer localProjectCount = getLocalProjectCount(searchCond);

        // 깃허브 프로젝트 수
        Integer githubProjectCount = getGithubProjectCount(searchCond);

        // 특허 프로젝트 수
        Integer patentProjectCount = getPatentProjectCount(searchCond);

        // 프로젝트 등록한 유저 수
        Integer projectUserCount = getProjectUserCount(searchCond);

        // 특허 등록한 유저 수
        Integer patentUserCount = getPatentUserCount(searchCond);

        // ProjectStatisticsResponse로 결과 반환
        return new ProjectStatisticsResponse(
                totalProjectCount != null ? totalProjectCount : 0,
                localProjectCount != null ? localProjectCount : 0,
                githubProjectCount != null ? githubProjectCount : 0,
                patentProjectCount != null ? patentProjectCount : 0,
                projectUserCount != null ? projectUserCount : 0,
                patentUserCount != null ? patentUserCount : 0
        );
    }

    // 전체 프로젝트 수 계산
    private Integer getTotalProjectCount(ProjectSearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(Expressions.numberTemplate(Integer.class,
                            "{0} + {1}",
                            departmentStatistics.projectCount.sum(),
                            departmentStatistics.githubProjectCount.sum()))
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(Expressions.numberTemplate(Integer.class,
                    "{0} + {1}",
                            collegeStatistics.projectCount.sum(),
                            collegeStatistics.githubProjectCount.sum()))
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(Expressions.numberTemplate(Integer.class,
                            "{0} + {1}",
                            userCountStatistics.totalProjectCount.sum(),
                            userCountStatistics.totalGithubProjectCount.sum()))
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 로컬 프로젝트 수 계산
    private Integer getLocalProjectCount(ProjectSearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.projectCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.projectCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.userProjectCount.sum())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 깃허브 프로젝트 수 계산
    private Integer getGithubProjectCount(ProjectSearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.githubProjectCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.githubProjectCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.totalGithubProjectCount.sum())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 특허 프로젝트 수 계산
    private Integer getPatentProjectCount(ProjectSearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.patentCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.patentCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.totalPatentCount.sum())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 프로젝트 등록한 유저 수 계산
    private Integer getProjectUserCount(ProjectSearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.projectUserCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.projectUserCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.userProjectCount.sum())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 특허 등록한 유저 수 계산
    private Integer getPatentUserCount(ProjectSearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.patentUserCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.patentUserCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.userPatentCount.sum())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 필터링 조건을 동적으로 적용하는 메서드
    private BooleanExpression applyFilters(ProjectSearchCond searchCond) {
        BooleanExpression predicate = null;

        // 조건이 없는 경우 UserCountStatistics에서 전체 조회
        if (searchCond.collegeIdx() == null && searchCond.departmentIdx() == null) {
            predicate = userCountStatistics.isNotNull();

            // 학기 필터링 (전체 조건에서도 적용 가능)
            if (searchCond.semesterIdx() != null) {
                predicate = predicate.and(userCountStatistics.semester.id.eq(searchCond.semesterIdx()));
            }

            // 분야 필터링 (전체 조건에서도 적용 가능)
            if (searchCond.fieldIdx() != null) {
                predicate = predicate.and(userCountStatistics.field.id.eq(searchCond.fieldIdx()));
            }
        }
        // 학과 조건이 있을 경우 DepartmentStatistics에서 필터링
        else if (searchCond.departmentIdx() != null) {
            predicate = departmentStatistics.isNotNull()
                    .and(departmentStatistics.department.id.eq(searchCond.departmentIdx()));

            // 학기 필터링
            if (searchCond.semesterIdx() != null) {
                predicate = predicate.and(departmentStatistics.semester.id.eq(searchCond.semesterIdx()));
            }

            // 분야 필터링
            if (searchCond.fieldIdx() != null) {
                predicate = predicate.and(departmentStatistics.field.id.eq(searchCond.fieldIdx()));
            }
        }
        // 단과대 조건이 있을 경우 CollegeStatistics에서 필터링
        else if (searchCond.collegeIdx() != null) {
            predicate = collegeStatistics.isNotNull()
                    .and(collegeStatistics.college.id.eq(searchCond.collegeIdx()));

            // 학기 필터링
            if (searchCond.semesterIdx() != null) {
                predicate = predicate.and(collegeStatistics.semester.id.eq(searchCond.semesterIdx()));
            }

            // 분야 필터링
            if (searchCond.fieldIdx() != null) {
                predicate = predicate.and(collegeStatistics.field.id.eq(searchCond.fieldIdx()));
            }
        }

        return predicate;
    }

}