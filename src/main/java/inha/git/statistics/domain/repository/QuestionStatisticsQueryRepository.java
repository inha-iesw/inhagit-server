package inha.git.statistics.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import inha.git.statistics.domain.enums.StatisticsType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static inha.git.category.domain.QCategory.category;
import static inha.git.college.domain.QCollege.college;
import static inha.git.department.domain.QDepartment.department;
import static inha.git.field.domain.QField.field;
import static inha.git.semester.domain.QSemester.semester;
import static inha.git.statistics.domain.QCollegeStatistics.collegeStatistics;
import static inha.git.statistics.domain.QDepartmentStatistics.departmentStatistics;
import static inha.git.statistics.domain.QStatistics.statistics;
import static inha.git.statistics.domain.QTotalCollegeStatistics.totalCollegeStatistics;
import static inha.git.statistics.domain.QTotalDepartmentStatistics.totalDepartmentStatistics;
import static inha.git.statistics.domain.QTotalUserStatistics.totalUserStatistics;
import static inha.git.statistics.domain.QUserCountStatistics.userCountStatistics;

/**
 * 질문 통계 조회 Repository
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class QuestionStatisticsQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 질문 통계 조회
     *
     * @param searchCond 검색 조건
     * @return QuestionStatisticsResponse
     */
    public QuestionStatisticsResponse getQuestionStatistics(SearchCond searchCond) {
        // 전체 질문 수 계산
        Integer questionCount = getQuestionCount(searchCond);
        // 멘토링 참여 인원 수 계산
        Integer userCount = getUserCount(searchCond);

        SearchCollegeResponse college = getCollege(searchCond.collegeIdx());
        SearchDepartmentResponse department = getDepartment(searchCond.departmentIdx());
        SearchFieldResponse field = getField(searchCond.fieldIdx());
        SearchSemesterResponse semester = getSemester(searchCond.semesterIdx());
        SearchCategoryResponse category = getCategory(searchCond.categoryIdx());

        return new QuestionStatisticsResponse(
                college,
                department,
                field,
                semester,
                category,
                questionCount != null ? questionCount : 0,
                userCount != null ? userCount : 0
        );
    }

    // Entity 조회 메서드들은 그대로 유지
    private SearchDepartmentResponse getDepartment(Integer departmentIdx) {
        if (departmentIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(SearchDepartmentResponse.class,
                        department.id,
                        department.name))
                .from(department)
                .where(department.id.eq(departmentIdx))
                .fetchOne();
    }

    private SearchCollegeResponse getCollege(Integer collegeIdx) {
        if (collegeIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(SearchCollegeResponse.class,
                        college.id,
                        college.name))
                .from(college)
                .where(college.id.eq(collegeIdx))
                .fetchOne();
    }

    private SearchFieldResponse getField(Integer fieldIdx) {
        if (fieldIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(SearchFieldResponse.class,
                        field.id,
                        field.name))
                .from(field)
                .where(field.id.eq(fieldIdx))
                .fetchOne();
    }

    private SearchSemesterResponse getSemester(Integer semesterIdx) {
        if (semesterIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(SearchSemesterResponse.class,
                        semester.id,
                        semester.name))
                .from(semester)
                .where(semester.id.eq(semesterIdx))
                .fetchOne();
    }

    private SearchCategoryResponse getCategory(Integer categoryIdx) {
        if (categoryIdx == null) return null;
        return queryFactory
                .select(Projections.constructor(SearchCategoryResponse.class,
                        category.id,
                        category.name))
                .from(category)
                .where(category.id.eq(categoryIdx))
                .fetchOne();
    }

    private Integer getQuestionCount(SearchCond searchCond) {
        return queryFactory
                .select(statistics.questionCount.sum())
                .from(statistics)
                .where(createStatisticsCondition(searchCond))
                .fetchOne();
    }

    private Integer getUserCount(SearchCond searchCond) {
        return queryFactory
                .select(statistics.questionParticipationCount.sum())
                .from(statistics)
                .where(createStatisticsCondition(searchCond))
                .fetchOne();
    }

    private BooleanBuilder createStatisticsCondition(SearchCond searchCond) {
        BooleanBuilder builder = new BooleanBuilder();

        // 1. StatisticsType 조건 설정
        if (searchCond.departmentIdx() != null) {
            builder.and(statistics.statisticsType.stringValue().eq(StatisticsType.DEPARTMENT.name()))
                    .and(statistics.targetId.eq(searchCond.departmentIdx()));
        } else if (searchCond.collegeIdx() != null) {
            builder.and(statistics.statisticsType.stringValue().eq(StatisticsType.COLLEGE.name()))
                    .and(statistics.targetId.eq(searchCond.collegeIdx()));
        } else {
            builder.and(statistics.statisticsType.stringValue().eq(StatisticsType.TOTAL.name()));
        }

        // 2. 차원 필터 조건 설정
        if (searchCond.semesterIdx() != null) {
            builder.and(statistics.semesterId.eq(searchCond.semesterIdx()));
        }
        if (searchCond.fieldIdx() != null) {
            builder.and(statistics.fieldId.eq(searchCond.fieldIdx()));
        }
        if (searchCond.categoryIdx() != null) {
            builder.and(statistics.categoryId.eq(searchCond.categoryIdx()));
        }

        // BooleanExpression으로 변환
        return builder;
    }
}
