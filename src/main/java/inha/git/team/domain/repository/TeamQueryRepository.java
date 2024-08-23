package inha.git.team.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.mapping.domain.QTeamUser;
import inha.git.mapping.domain.TeamUser;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.team.api.controller.dto.response.SearchMyTeamsResponse;
import inha.git.team.domain.QTeam;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 팀 조회 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class TeamQueryRepository {

    private final JPAQueryFactory queryFactory;


    /**
     * 사용자가 참여 중인 팀 목록 조회
     *
     * @param userId   사용자 ID
     * @param pageable 페이지 정보
     * @return 사용자가 참여 중인 팀 목록 페이지
     */
    public Page<SearchMyTeamsResponse> getUserTeams(Integer userId, Pageable pageable) {
        QTeam team = QTeam.team;
        QTeamUser teamUser = QTeamUser.teamUser;
        QUser user = QUser.user;

        JPAQuery<TeamUser> query = queryFactory
                .select(teamUser)
                .from(teamUser)
                .join(teamUser.team, team).fetchJoin()
                .join(team.user, user).fetchJoin()
                .where(teamUser.user.id.eq(userId))
                .orderBy(teamUser.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<TeamUser> teamUsers = query.fetch();
        long total = query.fetchCount();

        List<SearchMyTeamsResponse> content = teamUsers.stream()
                .map(tu -> new SearchMyTeamsResponse(
                        tu.getTeam().getId(),
                        tu.getTeam().getName(),
                        tu.getTeam().getCreatedAt(),
                        tu.getCreatedAt(),
                        new SearchUserResponse(
                                tu.getTeam().getUser().getId(),
                                tu.getTeam().getUser().getName()
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }
}
