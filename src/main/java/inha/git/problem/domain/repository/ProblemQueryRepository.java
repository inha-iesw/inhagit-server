package inha.git.problem.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.domain.Problem;
import inha.git.problem.domain.QProblem;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProblemQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<SearchProblemsResponse> getProblems(Pageable pageable) {
        QProblem problem = QProblem.problem;
        QUser user = QUser.user;
        JPAQuery<Problem> query = queryFactory
                .select(problem)
                .from(problem)
                .leftJoin(problem.user, user)
                .where(problem.state.eq(Problem.State.ACTIVE))
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
                       0,
                        new SearchUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName()
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }
}
