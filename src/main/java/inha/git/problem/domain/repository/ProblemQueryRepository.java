package inha.git.problem.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.common.BaseEntity;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchTeamRequestProblemResponse;
import inha.git.problem.api.controller.dto.response.SearchUserRequestProblemResponse;
import inha.git.problem.domain.*;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.team.domain.QTeam;
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

    /**
     * 문제 신청 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 문제 신청 목록
     */
    public Page<SearchRequestProblemResponse> getRequestProblems(Pageable pageable) {
        QProblemRequest problemRequest = QProblemRequest.problemRequest;
        QProblemPersonalRequest personalRequest = QProblemPersonalRequest.problemPersonalRequest;
        QProblemTeamRequest teamRequest = QProblemTeamRequest.problemTeamRequest;

        // 별칭을 다르게 지정
        QUser personalUser = new QUser("personalUser");
        QUser teamLeaderUser = new QUser("teamLeaderUser");
        QTeam team = QTeam.team;

        JPAQuery<ProblemRequest> query = queryFactory
                .selectFrom(problemRequest)
                .leftJoin(problemRequest.personalRequest, personalRequest)
                .leftJoin(personalRequest.user, personalUser)
                .leftJoin(problemRequest.teamRequest, teamRequest)
                .leftJoin(teamRequest.team, team)
                .leftJoin(team.user, teamLeaderUser)
                .where(problemRequest.state.eq(BaseEntity.State.ACTIVE))
                .orderBy(problemRequest.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<ProblemRequest> problemRequests = query.fetch();
        long total = query.fetchCount();

        List<SearchRequestProblemResponse> content = problemRequests.stream()
                .map(pr -> {
                    SearchUserRequestProblemResponse userResponse = null;
                    SearchTeamRequestProblemResponse teamResponse = null;

                    if (pr.getPersonalRequest() != null) {
                        userResponse = new SearchUserRequestProblemResponse(
                                pr.getPersonalRequest().getUser().getId(),
                                pr.getPersonalRequest().getUser().getName()
                        );
                    }

                    if (pr.getTeamRequest() != null) {
                        teamResponse = new SearchTeamRequestProblemResponse(
                                pr.getTeamRequest().getTeam().getId(),
                                pr.getTeamRequest().getTeam().getName(),
                                new SearchUserResponse(
                                        pr.getTeamRequest().getTeam().getUser().getId(),
                                        pr.getTeamRequest().getTeam().getUser().getName()
                                ),
                                pr.getTeamRequest().getTeam().getTeamUsers().stream()
                                        .map(tu -> new SearchUserResponse(tu.getUser().getId(), tu.getUser().getName()))
                                        .toList()
                        );
                    }
                    return new SearchRequestProblemResponse(
                            pr.getId(),
                            pr.getType(),
                            pr.getCreatedAt(),
                            pr.getAcceptAt(),
                            userResponse,
                            teamResponse
                    );
                })
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
