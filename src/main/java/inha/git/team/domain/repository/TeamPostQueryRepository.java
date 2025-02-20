package inha.git.team.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.team.api.controller.dto.response.SearchTeamPostTeamResponse;
import inha.git.team.api.controller.dto.response.SearchTeamPostsResponse;
import inha.git.team.domain.QTeam;
import inha.git.team.domain.QTeamPost;
import inha.git.team.domain.TeamPost;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inha.git.common.Constant.mapRoleToPosition;

/**
 * 팀 게시글 조회 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class TeamPostQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 팀 게시글 목록 조회
     *
     * @param pageable 페이지 정보
     * @return 팀 게시글 페이지
     */
    public Page<SearchTeamPostsResponse> getTeamPosts(Pageable pageable) {
        QTeamPost teamPost = QTeamPost.teamPost;
        QTeam team = QTeam.team;
        QUser user = QUser.user;

        JPAQuery<TeamPost> query = queryFactory
                .select(teamPost)
                .from(teamPost)
                .leftJoin(teamPost.team, team)
                .leftJoin(team.user, user)
                .where(teamPost.state.eq(TeamPost.State.ACTIVE))
                .orderBy(teamPost.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        List<TeamPost> teamPosts = query.fetch();
        long total = query.fetchCount();

        List<SearchTeamPostsResponse> content = teamPosts.stream()
                .map(q -> new SearchTeamPostsResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getCreatedAt(),
                        new SearchTeamPostTeamResponse(
                                q.getTeam().getId(),
                                q.getTeam().getName(),
                                new SearchUserResponse(
                                        q.getTeam().getUser().getId(),
                                        q.getTeam().getUser().getName(),
                                        mapRoleToPosition(q.getTeam().getUser().getRole())
                                )
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }
}
