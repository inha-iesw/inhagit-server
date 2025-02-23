package inha.git.problem.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.problem.api.controller.dto.response.SearchProblemsResponse;
import inha.git.problem.api.controller.dto.response.SearchRequestProblemsResponse;
import inha.git.problem.domain.*;
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
                        new SearchUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName(),
                                mapRoleToPosition(p.getUser().getRole())
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
    public Page<SearchRequestProblemsResponse> getRequestProblems(Integer problemIdx, Pageable pageable) {

        return null;
    }

    /**
     * 사용자가 신청한 문제 목록 조회
     *
     * @param userId 사용자 인덱스
     * @param pageable 페이징 정보
     * @return 사용자가 신청한 문제 목록
     */
    public Page<SearchProblemsResponse> getUserProblems(Integer userId, Pageable pageable) {
//        QProblem problem = QProblem.problem;
//        QProblemRequest problemRequest = QProblemRequest.problemRequest;
//        QTeamUser teamUser = QTeamUser.teamUser;
//        QTeam team = QTeam.team;
//        QUser user = QUser.user;
//
//        JPAQuery<Problem> query = queryFactory
//                .selectDistinct(problem)  // 중복을 제거하기 위해 distinct 사용
//                .from(problemRequest)
//                .leftJoin(problemRequest.personalRequest, personalRequest)
//                .leftJoin(problemRequest.teamRequest, teamRequest)
//                .leftJoin(personalRequest.user, user)  // 개인 신청의 경우 유저 조인
//                .leftJoin(teamRequest.team, team)      // 팀 신청의 경우 팀 조인
//                .leftJoin(team.teamUsers, teamUser)    // 팀에 속한 유저 조인 (팀원 여부 확인)
//                .leftJoin(problemRequest.problem, problem)
//                .where(problemRequest.state.eq(ACTIVE)
//                        .and(problemRequest.acceptAt.isNotNull())  // 승인된 요청만 조회
//                        .and(
//                                personalRequest.user.id.eq(userId)     // 개인 신청의 경우 유저 필터링
//                                        .or(team.user.id.eq(userId))           // 팀장이 해당 유저일 경우
//                                        .or(teamUser.user.id.eq(userId))       // 팀에 속한 유저일 경우
//                        ))
//                .orderBy(problem.id.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize());
//
//        List<Problem> problems = query.fetch();
//        long total = query.fetchCount();
//
//        List<SearchProblemsResponse> content = problems.stream()
//                .map(p -> new SearchProblemsResponse(
//                        p.getId(),
//                        p.getTitle(),
//                        p.getCreatedAt(),
//                        0,
//                        new SearchUserResponse(
//                                p.getUser().getId(),
//                                p.getUser().getName(),
//                                mapRoleToPosition(p.getUser().getRole())
//                        )
//                ))
//                .toList();

       // return new PageImpl<>(content, pageable, total);
        return null;
    }
}
