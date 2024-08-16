package inha.git.project.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.mapping.domain.QProjectField;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.QProject;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 프로젝트 목록 조회
     *
     * @param pageable 페이지 정보
     * @return 프로젝트 페이지
     */
    public Page<SearchProjectsResponse> getProjects(Pageable pageable) {
        QProject project = QProject.project;
        QUser user = QUser.user;
        QProjectField projectField = QProjectField.projectField;

        // 프로젝트 목록 조회 쿼리
        JPAQuery<Project> query = queryFactory
                .select(project)
                .from(project)
                .leftJoin(project.user, user)
                .leftJoin(project.projectFields, projectField)
                .where(project.state.eq(Project.State.ACTIVE))
                .orderBy(project.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 결과 리스트 및 총 개수 가져오기
        List<Project> projects = query.fetch();
        long total = query.fetchCount();
        // SearchProjectsResponse 변환
        List<SearchProjectsResponse> content = projects.stream()
                .map(p -> new SearchProjectsResponse(
                        p.getId(),
                        p.getTitle(),
                        p.getCreatedAt(),
                        p.getProjectFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName()
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }
}
