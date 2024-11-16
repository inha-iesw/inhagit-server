package inha.git.search.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.notice.domain.QNotice;
import inha.git.problem.domain.QProblem;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.question.domain.QQuestion;
import inha.git.search.api.controller.dto.response.SearchResponse;
import inha.git.search.domain.enums.TableType;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.team.domain.QTeamPost;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.project.domain.QProject.project;
import static inha.git.user.domain.QUser.user;

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
     * @param type     검색할 테이블 타입 (null일 경우 전체 검색)
     * @return 검색 결과
     */
    public Page<SearchResponse> search(String search, Pageable pageable, TableType type) {
        List<SearchResponse> results;

        if (type == null) {
            // type이 null일 경우 전체 조회
            results = getAllResults(search, pageable);
        } else {
            // type이 지정된 경우 해당 type만 조회
            results = getResultsByType(search, pageable, type);
        }

        return new PageImpl<>(results, pageable, results.size());
    }

    /**
     * 전체 결과 조회
     */
    private List<SearchResponse> getAllResults(String search, Pageable pageable) {
        // 각 타입별로 조회한 결과를 모두 합침
        List<SearchResponse> projectResults = getProjectResults(search, pageable);
        List<SearchResponse> problemResults = getProblemResults(search, pageable);
        List<SearchResponse> questionResults = getQuestionResults(search, pageable);
        List<SearchResponse> teamResults = getTeamResults(search, pageable);
        List<SearchResponse> noticeResults = getNoticeResults(search, pageable);

        return Stream.of(projectResults, problemResults, questionResults, teamResults, noticeResults)
                .flatMap(List::stream)
                .sorted((a, b) -> b.createdAt().compareTo(a.createdAt()))  // 날짜 기준 내림차순 정렬
                .limit(pageable.getPageSize())
                .toList();
    }

    /**
     * 특정 type에 해당하는 결과만 조회
     */
    private List<SearchResponse> getResultsByType(String search, Pageable pageable, TableType type) {
        return switch (type) {
            case I_FOSS -> getProjectResults(search, pageable);
            case PROBLEM -> getProblemResults(search, pageable);
            case ISSS -> getQuestionResults(search, pageable);
            case TEAM -> getTeamResults(search, pageable);
            case NOTICE -> getNoticeResults(search, pageable);
        };
    }

    /**
     * 프로젝트 검색 결과
     */
    private List<SearchResponse> getProjectResults(String search, Pageable pageable) {
        return queryFactory
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
                        new SearchUserResponse(p.getUser().getId(), p.getUser().getName(), mapRoleToPosition(p.getUser().getRole())),
                        new SearchSemesterResponse(p.getSemester().getId(), p.getSemester().getName()),
                        p.getSubjectName(),
                        p.getLikeCount(),
                        p.getProjectFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        TableType.I_FOSS.getValue(),
                        p.getRepoName() != null)
                )
                .toList();
    }

    /**
     * 문제 검색 결과
     */
    private List<SearchResponse> getProblemResults(String search, Pageable pageable) {
        QProblem problem = QProblem.problem;
        return queryFactory
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
                        new SearchUserResponse(p.getUser().getId(), p.getUser().getName(), mapRoleToPosition(p.getUser().getRole())),
                        null,
                        null,
                        null,
                        null,
                        TableType.PROBLEM.getValue(),
                        null))
                .toList();
    }

    /**
     * 질문 검색 결과
     */
    private List<SearchResponse> getQuestionResults(String search, Pageable pageable) {
        QQuestion question = QQuestion.question;
        return queryFactory
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
                        new SearchUserResponse(q.getUser().getId(), q.getUser().getName(), mapRoleToPosition(q.getUser().getRole())),
                        new SearchSemesterResponse(q.getSemester().getId(), q.getSemester().getName()),
                        q.getSubjectName(),
                        q.getLikeCount(),
                        q.getQuestionFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        TableType.ISSS.getValue(),
                        null))
                .toList();
    }

    /**
     * 팀 포스트 검색 결과
     */
    private List<SearchResponse> getTeamResults(String search, Pageable pageable) {
        QTeamPost teamPost = QTeamPost.teamPost;
        return queryFactory
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
                        new SearchUserResponse(t.getTeam().getUser().getId(), t.getTeam().getUser().getName(), mapRoleToPosition(t.getTeam().getUser().getRole())),
                        null,
                        null,
                        null,
                        null,
                        TableType.TEAM.getValue(),
                        null))
                .toList();
    }

    /**
     * 공지 검색 결과
     */
    private List<SearchResponse> getNoticeResults(String search, Pageable pageable) {
        QNotice notice = QNotice.notice;
        return queryFactory
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
                        new SearchUserResponse(n.getUser().getId(), n.getUser().getName(), mapRoleToPosition(n.getUser().getRole())),
                        null,
                        null,
                        null,
                        null,
                        TableType.NOTICE.getValue(),
                        null))
                .toList();
    }
}