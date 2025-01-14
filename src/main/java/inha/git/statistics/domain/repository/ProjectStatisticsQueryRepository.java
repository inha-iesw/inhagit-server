package inha.git.statistics.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.controller.dto.data.StatisticsCounts;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.ProjectStatisticsResponse;
import inha.git.statistics.domain.enums.StatisticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static inha.git.category.domain.QCategory.category;
import static inha.git.college.domain.QCollege.college;
import static inha.git.department.domain.QDepartment.department;
import static inha.git.field.domain.QField.field;
import static inha.git.semester.domain.QSemester.semester;
import static inha.git.statistics.domain.QStatistics.statistics;

/**
 * StatisticsQueryRepository는 통계 쿼리를 처리하는 레포지토리.
 */
@Repository
@RequiredArgsConstructor
public class ProjectStatisticsQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ProjectStatisticsResponse getProjectStatistics(SearchCond searchCond) {
        // 1. 기본 정보 조회
        SearchCollegeResponse college = getCollege(searchCond.collegeIdx());
        SearchDepartmentResponse department = getDepartment(searchCond.departmentIdx());
        SearchFieldResponse field = getField(searchCond.fieldIdx());
        SearchSemesterResponse semester = getSemester(searchCond.semesterIdx());
        SearchCategoryResponse category = getCategory(searchCond.categoryIdx());

        // 2. 통계 데이터 조회
        StatisticsCounts counts = getStatisticsCounts(searchCond);

        return new ProjectStatisticsResponse(
                college,
                department,
                field,
                semester,
                category,
                counts.totalCount(),
                counts.localCount(),
                counts.githubCount(),
                counts.userCount()
        );
    }

    private StatisticsCounts getStatisticsCounts(SearchCond searchCond) {
        BooleanBuilder whereClause = new BooleanBuilder();

        // 1. 기본 필터 조건 설정
        if (searchCond.departmentIdx() != null) {
            whereClause.and(statistics.statisticsType.eq(StatisticsType.DEPARTMENT))
                    .and(statistics.targetId.eq(searchCond.departmentIdx()));
        } else if (searchCond.collegeIdx() != null) {
            whereClause.and(statistics.statisticsType.eq(StatisticsType.COLLEGE))
                    .and(statistics.targetId.eq(searchCond.collegeIdx()));
        } else {
            whereClause.and(statistics.statisticsType.eq(StatisticsType.TOTAL));
        }

        // 2. 추가 필터 조건 설정
        if (searchCond.semesterIdx() != null) {
            whereClause.and(statistics.semesterId.eq(searchCond.semesterIdx()));
        }
        if (searchCond.fieldIdx() != null) {
            whereClause.and(statistics.fieldId.eq(searchCond.fieldIdx()));
        }
        if (searchCond.categoryIdx() != null) {
            whereClause.and(statistics.categoryId.eq(searchCond.categoryIdx()));
        }

        // 3. 통계 데이터 조회
        Tuple result = queryFactory
                .select(
                        statistics.localProjectCount.sum(),
                        statistics.githubProjectCount.sum(),
                        statistics.projectParticipationCount.sum()
                )
                .from(statistics)
                .where(whereClause)
                .fetchOne();

        // 4. 결과 처리
        Integer localCount = result.get(statistics.localProjectCount.sum());
        Integer githubCount = result.get(statistics.githubProjectCount.sum());
        Integer userCount = result.get(statistics.projectParticipationCount.sum());

        // null 체크 및 기본값 설정
        localCount = (localCount != null) ? localCount : 0;
        githubCount = (githubCount != null) ? githubCount : 0;
        userCount = (userCount != null) ? userCount : 0;

        return new StatisticsCounts(
                localCount + githubCount,  // 전체 프로젝트 수
                localCount,                // 로컬 프로젝트 수
                githubCount,               // 깃허브 프로젝트 수
                userCount                  // 프로젝트 참여 유저 수
        );
    }

    // Entity 조회 메서드들은 동일하게 유지
    private SearchDepartmentResponse getDepartment(Integer departmentIdx) {
        if (departmentIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(
                        SearchDepartmentResponse.class,
                        department.id,
                        department.name
                ))
                .from(department)
                .where(department.id.eq(departmentIdx))
                .fetchOne();
    }

    private SearchCollegeResponse getCollege(Integer collegeIdx) {
        if (collegeIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(
                        SearchCollegeResponse.class,
                        college.id,
                        college.name
                ))
                .from(college)
                .where(college.id.eq(collegeIdx))
                .fetchOne();
    }

    private SearchFieldResponse getField(Integer fieldIdx) {
        if (fieldIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(
                        SearchFieldResponse.class,
                        field.id,
                        field.name
                ))
                .from(field)
                .where(field.id.eq(fieldIdx))
                .fetchOne();
    }

    private SearchSemesterResponse getSemester(Integer semesterIdx) {
        if (semesterIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(
                        SearchSemesterResponse.class,
                        semester.id,
                        semester.name
                ))
                .from(semester)
                .where(semester.id.eq(semesterIdx))
                .fetchOne();
    }

    private SearchCategoryResponse getCategory(Integer categoryIdx) {
        if (categoryIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(
                        SearchCategoryResponse.class,
                        category.id,
                        category.name
                ))
                .from(category)
                .where(category.id.eq(categoryIdx))
                .fetchOne();
    }
}
