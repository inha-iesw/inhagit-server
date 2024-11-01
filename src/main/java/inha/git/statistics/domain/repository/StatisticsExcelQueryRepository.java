package inha.git.statistics.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.domain.StatisticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static inha.git.category.domain.QCategory.category;
import static inha.git.college.domain.QCollege.college;
import static inha.git.department.domain.QDepartment.department;
import static inha.git.field.domain.QField.field;
import static inha.git.semester.domain.QSemester.semester;
import static inha.git.statistics.domain.QCollegeStatistics.collegeStatistics;
import static inha.git.statistics.domain.QDepartmentStatistics.departmentStatistics;
import static inha.git.statistics.domain.QTotalCollegeStatistics.totalCollegeStatistics;
import static inha.git.statistics.domain.QTotalDepartmentStatistics.totalDepartmentStatistics;
import static inha.git.statistics.domain.QTotalUserStatistics.totalUserStatistics;
import static inha.git.statistics.domain.QUserCountStatistics.userCountStatistics;

/**
 * StatisticsExcelQueryRepository 통계 쿼리를 처리하는 레포지토리.
 */
@Repository
@RequiredArgsConstructor
public class StatisticsExcelQueryRepository {
    private final JPAQueryFactory queryFactory;

    public boolean hasNonZeroStatistics(StatisticsType type, Integer id) {
        switch (type) {
            case DEPARTMENT -> {
                return queryFactory
                        .select(Expressions.constant(1))
                        .from(totalDepartmentStatistics)
                        .where(
                                totalDepartmentStatistics.departmentId.eq(id),
                                totalDepartmentStatistics.totalProjectCount.gt(0)
                                        .or(totalDepartmentStatistics.totalGithubProjectCount.gt(0))
                                        .or(totalDepartmentStatistics.totalPatentCount.gt(0))
                                        .or(totalDepartmentStatistics.userProjectCount.gt(0))
                                        .or(totalDepartmentStatistics.userPatentCount.gt(0))
                                        .or(totalDepartmentStatistics.totalQuestionCount.gt(0))
                                        .or(totalDepartmentStatistics.userQuestionCount.gt(0))
                        )
                        .fetchFirst() != null;
            }
            case COLLEGE -> {
                return queryFactory
                        .select(Expressions.constant(1))
                        .from(totalCollegeStatistics)
                        .where(
                                totalCollegeStatistics.collegeId.eq(id),
                                totalCollegeStatistics.totalProjectCount.gt(0)
                                        .or(totalCollegeStatistics.totalGithubProjectCount.gt(0))
                                        .or(totalCollegeStatistics.totalPatentCount.gt(0))
                                        .or(totalCollegeStatistics.userProjectCount.gt(0))
                                        .or(totalCollegeStatistics.userPatentCount.gt(0))
                                        .or(totalCollegeStatistics.totalQuestionCount.gt(0))
                                        .or(totalCollegeStatistics.userQuestionCount.gt(0))
                        )
                        .fetchFirst() != null;
            }
            case TOTAL -> {
                return queryFactory
                        .select(Expressions.constant(1))
                        .from(totalUserStatistics)
                        .where(
                                totalUserStatistics.totalProjectCount.gt(0)
                                        .or(totalUserStatistics.totalGithubProjectCount.gt(0))
                                        .or(totalUserStatistics.totalPatentCount.gt(0))
                                        .or(totalUserStatistics.userProjectCount.gt(0))
                                        .or(totalUserStatistics.userPatentCount.gt(0))
                                        .or(totalUserStatistics.totalQuestionCount.gt(0))
                                        .or(totalUserStatistics.userQuestionCount.gt(0))
                        )
                        .fetchFirst() != null;
            }
        }
        return false;
    }
}
