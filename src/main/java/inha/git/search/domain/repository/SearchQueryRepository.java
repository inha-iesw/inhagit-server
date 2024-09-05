package inha.git.search.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.notice.domain.QNotice;
import inha.git.problem.domain.QProblem;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.project.domain.QProject;
import inha.git.question.domain.QQuestion;
import inha.git.search.api.controller.dto.response.SearchResponse;
import inha.git.search.domain.enums.TableType;
import inha.git.team.domain.QTeamPost;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

/**
 * SearchQueryRepository는 검색 쿼리를 처리하는 레포지토리.
 */
@Repository
@RequiredArgsConstructor
public class SearchQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 검색 결과
     */
    public Page<SearchResponse> search(String search, Pageable pageable) {

        QProject project = QProject.project;
        QUser user = QUser.user;
        List<SearchResponse> projectResults = queryFactory
                .selectFrom(project)
                .join(project.user, user)
                .where(
                        project.contents.containsIgnoreCase(search)
                                .or(project.title.containsIgnoreCase(search)),
                        project.deletedAt.isNull(),
                        user.deletedAt.isNull()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .stream()
                .map(p -> new SearchResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getCreatedAt(),
                        new SearchUserResponse(p.getUser().getId(), p.getUser().getName()),
                        TableType.I_FOSS.getValue()))
                .toList();

        // Problem 검색
        QProblem problem = QProblem.problem;
        List<SearchResponse> problemResults = queryFactory
                .selectFrom(problem)
                .join(problem.user, user)
                .where(
                        problem.contents.containsIgnoreCase(search)
                                .or(problem.title.containsIgnoreCase(search)),
                        problem.deletedAt.isNull(),
                        user.deletedAt.isNull()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .stream()
                .map(p -> new SearchResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getCreatedAt(),
                        new SearchUserResponse(p.getUser().getId(), p.getUser().getName()),
                        TableType.PROBLEM.getValue()))
                .toList();

        // Question 검색
        QQuestion question = QQuestion.question;
        List<SearchResponse> questionResults = queryFactory
                .selectFrom(question)
                .join(question.user, user)
                .where(
                        question.contents.containsIgnoreCase(search)
                                .or(question.title.containsIgnoreCase(search)),
                        question.deletedAt.isNull(),
                        user.deletedAt.isNull()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .stream()
                .map(q -> new SearchResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getCreatedAt(),
                        new SearchUserResponse(q.getUser().getId(), q.getUser().getName()),
                        TableType.ISSS.getValue()))
                .toList();

        // Team Post 검색
        QTeamPost teamPost = QTeamPost.teamPost;
        List<SearchResponse> teamResults = queryFactory
                .selectFrom(teamPost)
                .join(teamPost.team.user, user)
                .where(
                        teamPost.contents.containsIgnoreCase(search)
                                .or(teamPost.title.containsIgnoreCase(search)),
                        teamPost.deletedAt.isNull(),
                        user.deletedAt.isNull()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .stream()
                .map(t -> new SearchResponse(
                        t.getId(),
                        t.getTitle(),
                        t.getCreatedAt(),
                        new SearchUserResponse(t.getTeam().getUser().getId(), t.getTeam().getUser().getName()),
                        TableType.TEAM.getValue()))
                .toList();

        // Notice 검색
        QNotice notice = QNotice.notice;
        List<SearchResponse> noticeResults = queryFactory
                .selectFrom(notice)
                .join(notice.user, user)
                .where(
                        notice.contents.containsIgnoreCase(search)
                                .or(notice.title.containsIgnoreCase(search)),
                        notice.deletedAt.isNull(),
                        user.deletedAt.isNull()
                )
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .stream()
                .map(n -> new SearchResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getCreatedAt(),
                        new SearchUserResponse(n.getUser().getId(), n.getUser().getName()),
                        TableType.NOTICE.getValue()))
                .toList();

        List<SearchResponse> allResults = Stream.of(projectResults, problemResults, questionResults, teamResults, noticeResults)
                .flatMap(List::stream)
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))  // 날짜 기준 내림차순 정렬
                .limit(pageable.getPageSize())
                .toList();

        return new PageImpl<>(allResults, pageable, allResults.size());
    }
}
