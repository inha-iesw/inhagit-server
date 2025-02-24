package inha.git.statistics.domain.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.controller.dto.response.PatentStatisticsResponse;
import inha.git.statistics.domain.enums.StatisticsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.semester.domain.QSemester.semester;
import static inha.git.statistics.domain.QStatistics.statistics;

@Repository
@RequiredArgsConstructor
public class PatentStatisticsQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<PatentStatisticsResponse> getPatentStatistics() {
        // 1. 모든 학기 정보 조회
        List<SearchSemesterResponse> semesters = queryFactory
                .select(Projections.constructor(SearchSemesterResponse.class,
                        semester.id,
                        semester.name))
                .from(semester)
                .where(semester.state.eq(ACTIVE))
                .orderBy(semester.id.asc())
                .fetch();

        Map<Integer, Integer> patentCountBySemester = queryFactory
                .select(statistics.semesterId,
                        Expressions.numberTemplate(Integer.class, "COALESCE(SUM({0}), 0)", statistics.patentCount))
                .from(statistics)
                .where(statistics.statisticsType.eq(StatisticsType.TOTAL))
                .groupBy(statistics.semesterId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Integer.class),
                        tuple -> tuple.get(1, Integer.class),
                        (a, b) -> a
                ));

        return semesters.stream()
                .map(semester -> new PatentStatisticsResponse(
                        semester,
                        patentCountBySemester.getOrDefault(semester.idx(), 0)
                ))
                .toList();
    }
}
