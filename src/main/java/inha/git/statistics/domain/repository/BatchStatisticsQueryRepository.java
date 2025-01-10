package inha.git.statistics.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.controller.dto.response.BatchCollegeStatisticsResponse;
import inha.git.statistics.api.controller.dto.response.CollegeStatisticsData;
import inha.git.statistics.api.controller.dto.response.SemesterStatistics;
import inha.git.statistics.domain.QStatistics;
import inha.git.statistics.domain.enums.StatisticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.college.domain.QCollege.college;
import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.semester.domain.QSemester.semester;

@Repository
@RequiredArgsConstructor
public class BatchStatisticsQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<BatchCollegeStatisticsResponse> getBatchStatistics() {
        // 1. 모든 단과대 정보 조회
        List<SearchCollegeResponse> colleges = queryFactory
                .select(Projections.constructor(SearchCollegeResponse.class,
                        college.id,
                        college.name))
                .from(college)
                .where(college.state.eq(ACTIVE))
                .fetch();

        // 2. 모든 학기 정보 조회
        List<SearchSemesterResponse> semesters = queryFactory
                .select(Projections.constructor(SearchSemesterResponse.class,
                        semester.id,
                        semester.name))
                .from(semester)
                .where(semester.state.eq(ACTIVE))
                .orderBy(semester.id.asc())
                .fetch();

        // 3. 새로운 Statistics 테이블에서 단과대별, 학기별 통계 데이터 조회
        List<CollegeStatisticsData> statistics = queryFactory
                .select(Projections.constructor(CollegeStatisticsData.class,
                        QStatistics.statistics.targetId,
                        QStatistics.statistics.semesterId,
                        Expressions.numberTemplate(Integer.class,
                                "COALESCE(SUM({0}), 0) + COALESCE(SUM({1}), 0)",
                                QStatistics.statistics.localProjectCount,
                                QStatistics.statistics.githubProjectCount),
                        Expressions.numberTemplate(Integer.class,
                                "COALESCE(SUM({0}), 0)",
                                QStatistics.statistics.localProjectCount),
                        Expressions.numberTemplate(Integer.class,
                                "COALESCE(SUM({0}), 0)",
                                QStatistics.statistics.githubProjectCount),
                        Expressions.numberTemplate(Integer.class,
                                "COALESCE(SUM({0}), 0)",
                                QStatistics.statistics.questionCount)))
                .from(QStatistics.statistics)
                .where(QStatistics.statistics.statisticsType.eq(StatisticsType.COLLEGE))
                .groupBy(QStatistics.statistics.targetId, QStatistics.statistics.semesterId)
                .fetch();

        // 4. 데이터 매핑 및 변환
        Map<Integer, List<CollegeStatisticsData>> statsByCollege = statistics.stream()
                .collect(Collectors.groupingBy(CollegeStatisticsData::collegeId));

        return colleges.stream()
                .map(college -> new BatchCollegeStatisticsResponse(
                        college,
                        mapToSemesterStatistics(semesters, statsByCollege.getOrDefault(college.idx(), List.of()))
                ))
                .toList();
    }

    private List<SemesterStatistics> mapToSemesterStatistics(
            List<SearchSemesterResponse> semesters,
            List<CollegeStatisticsData> collegeStats) {

        Map<Integer, CollegeStatisticsData> statsBySemester = collegeStats.stream()
                .collect(Collectors.toMap(
                        CollegeStatisticsData::semesterId,
                        stats -> stats,
                        (existing, replacement) -> existing
                ));

        return semesters.stream()
                .map(semester -> {
                    CollegeStatisticsData stats = statsBySemester.getOrDefault(
                            semester.idx(),
                            new CollegeStatisticsData(null, semester.idx(), 0, 0, 0, 0)
                    );

                    return new SemesterStatistics(
                            semester,
                            stats.totalProjectCount(),
                            stats.localProjectCount(),
                            stats.githubProjectCount(),
                            stats.questionCount()
                    );
                })
                .toList();
    }
}