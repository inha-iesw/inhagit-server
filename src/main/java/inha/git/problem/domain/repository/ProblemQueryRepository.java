package inha.git.problem.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemsResponse;
import inha.git.problem.domain.*;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.problem.domain.QProblem.problem;
import static inha.git.problem.domain.QProblemRequest.problemRequest;
import static inha.git.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class ProblemQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<SearchProblemsResponse> getProblems(Pageable pageable) {
        JPAQuery<Problem> query = queryFactory
                .select(problem)
                .from(problem)
                .leftJoin(problem.user, user)
                .where(problem.state.eq(ACTIVE))
                .orderBy(problem.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Problem> problems = query.fetch();
        long total = query.fetchCount();

        List<SearchProblemsResponse> content = problems.stream()
                .map(p -> new SearchProblemsResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getCreatedAt(),
                        p.getParticipantCount(),
                        p.getStatus(),
                        p.getHasAttachment(),
                        p.getProblemFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName(),
                                mapRoleToPosition(p.getUser().getRole())
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }

    public Page<SearchProblemsResponse> getUserProblems(Integer userId, Pageable pageable) {
        JPAQuery<Problem> query = queryFactory
                .select(problemRequest.problem)
                .from(problemRequest)
                .where(
                        problemRequest.user.id.eq(userId),
                        problemRequest.state.eq(ACTIVE)
                )
                .orderBy(problemRequest.problem.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Problem> problems = query.fetch();
        Long total = queryFactory
                .select(problemRequest.problem.countDistinct())
                .from(problemRequest)
                .where(
                        problemRequest.user.id.eq(userId),
                        problemRequest.state.eq(ACTIVE)
                )
                .fetchOne();

        return new PageImpl<>(
                problems.stream()
                        .map(this::convertToSearchProblemsResponse)
                        .toList(),
                pageable,
                total != null ? total : 0
        );
    }

    private SearchProblemsResponse convertToSearchProblemsResponse(Problem problem) {
        return new SearchProblemsResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getCreatedAt(),
                problem.getParticipantCount(),
                problem.getStatus(),
                problem.getHasAttachment(),
                problem.getProblemFields().stream()
                        .map(f -> new SearchFieldResponse(
                                f.getField().getId(),
                                f.getField().getName()
                        ))
                        .toList(),
                new inha.git.project.api.controller.dto.response.SearchUserResponse(
                        problem.getUser().getId(),
                        problem.getUser().getName(),
                        mapRoleToPosition(problem.getUser().getRole())
                )
        );
    }
}
