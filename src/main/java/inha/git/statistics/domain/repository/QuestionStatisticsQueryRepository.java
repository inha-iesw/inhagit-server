package inha.git.statistics.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchDepartmentResponse;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.controller.dto.request.SearchCond;
import inha.git.statistics.api.controller.dto.response.QuestionStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static inha.git.college.domain.QCollege.college;
import static inha.git.department.domain.QDepartment.department;
import static inha.git.field.domain.QField.field;
import static inha.git.semester.domain.QSemester.semester;
import static inha.git.statistics.domain.QCollegeStatistics.collegeStatistics;
import static inha.git.statistics.domain.QDepartmentStatistics.departmentStatistics;
import static inha.git.statistics.domain.QUserCountStatistics.userCountStatistics;

/**
 * 질문 통계 조회 Repository
 */
@Repository
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
        return new QuestionStatisticsResponse(
                college,
                department,
                field,
                semester,
                questionCount != null ? questionCount : 0,
                userCount != null ? userCount : 0
        );
    }

    private SearchDepartmentResponse getDepartment(Integer departmentIdx) {
        if (departmentIdx == null) {
            return null; // departmentIdx가 null인 경우 null 반환
        }

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
        if (collegeIdx == null) {
            return null; // collegeIdx가 null인 경우 null 반환
        }

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
        if (fieldIdx == null) {
            return null; // fieldIdx가 null인 경우 null 반환
        }

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
        if (semesterIdx == null) {
            return null; // semesterIdx가 null인 경우 null 반환
        }

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

    // 질문 수 계산
    private Integer getQuestionCount(SearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.questionCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.questionCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.totalQuestionCount.sum())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 멘토링 참여 인원 수 계산
    private Integer getUserCount(SearchCond searchCond) {
        if (searchCond.departmentIdx() != null) {
            return queryFactory
                    .select(departmentStatistics.questionUserCount.sum())
                    .from(departmentStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else if (searchCond.collegeIdx() != null) {
            return queryFactory
                    .select(collegeStatistics.questionUserCount.sum())
                    .from(collegeStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        } else {
            return queryFactory
                    .select(userCountStatistics.userQuestionCount.max())
                    .from(userCountStatistics)
                    .where(applyFilters(searchCond))
                    .fetchOne();
        }
    }

    // 필터링 조건을 동적으로 적용하는 메서드
    private BooleanExpression applyFilters(SearchCond searchCond) {
        BooleanExpression predicate = null;

        // 조건이 없는 경우 UserCountStatistics에서 전체 조회
        if (searchCond.collegeIdx() == null && searchCond.departmentIdx() == null) {
            predicate = userCountStatistics.isNotNull();

            // 학기 필터링
            if (searchCond.semesterIdx() != null) {
                predicate = predicate.and(userCountStatistics.semester.id.eq(searchCond.semesterIdx()));
            }

            // 분야 필터링
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