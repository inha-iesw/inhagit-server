package inha.git.project.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.mapping.domain.QProjectField;
import inha.git.project.api.controller.dto.request.SearchProjectCond;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchPatentSummaryResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.QProject;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inha.git.category.domain.QCategory.category;
import static inha.git.college.domain.QCollege.college;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.department.domain.QDepartment.department;
import static inha.git.mapping.domain.QProjectField.projectField;
import static inha.git.mapping.domain.QUserDepartment.userDepartment;
import static inha.git.project.domain.QProject.project;
import static inha.git.semester.domain.QSemester.semester;
import static inha.git.user.domain.QUser.user;

/**
 * 프로젝트 조회 관련 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 유저의 프로젝트 목록 조회
     *
     * @param id       유저 ID
     * @param pageable 페이지 정보
     * @return 프로젝트 페이지
     */
    public Page<SearchProjectsResponse> getUserProjects(Integer id, Pageable pageable) {
        QProject project = QProject.project;
        QUser user = QUser.user;
        QProjectField projectField = QProjectField.projectField;

        // 프로젝트 목록 조회 쿼리
        JPAQuery<Project> query = queryFactory
                .select(project)
                .from(project)
                .leftJoin(project.user, user)
                .leftJoin(project.projectFields, projectField)
                .where(project.state.eq(Project.State.ACTIVE).and(user.id.eq(id)))
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
                        p.getRepoName() != null,
                        new SearchSemesterResponse(
                                p.getSemester().getId(),
                                p.getSemester().getName()),
                        new SearchCategoryResponse(
                                p.getCategory().getId(),
                                p.getCategory().getName()),
                        p.getSubjectName(),
                        p.getLikeCount(),
                        p.getCommentCount(),
                        p.getIsPublic(),
                        p.getProjectFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName(),
                                mapRoleToPosition(p.getUser().getRole())
                        ),
                        new SearchPatentSummaryResponse(
                                p.getProjectPatent() != null ? p.getProjectPatent().getId() : null,
                                p.getProjectPatent() != null && p.getProjectPatent().getAcceptAt() != null
                        ))).toList();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 조건에 따른 프로젝트 목록 조회
     *
     * @param searchProjectCond 검색 조건
     * @param pageable          페이지 정보
     * @return 프로젝트 페이지
     */
    public Page<SearchProjectsResponse> getCondProjects(SearchProjectCond searchProjectCond, Pageable pageable) {

        // 동적 조건 생성
        BooleanExpression condition = project.state.eq(Project.State.ACTIVE);

        // User와 Department 매핑을 통한 조건 추가
        if (searchProjectCond.collegeIdx() != null) {
            condition = condition.and(userDepartment.department.college.id.eq(searchProjectCond.collegeIdx()));
        }

        if (searchProjectCond.departmentIdx() != null) {
            condition = condition.and(userDepartment.department.id.eq(searchProjectCond.departmentIdx()));
        }

        if (searchProjectCond.semesterIdx() != null) {
            condition = condition.and(project.semester.id.eq(searchProjectCond.semesterIdx()));
        }

        if (searchProjectCond.categoryIdx() != null) {
            condition = condition.and(project.category.id.eq(searchProjectCond.categoryIdx()));
        }

        if (searchProjectCond.fieldIdx() != null) {
            condition = condition.and(projectField.field.id.eq(searchProjectCond.fieldIdx()));
        }

        if (searchProjectCond.subject() != null && !searchProjectCond.subject().isEmpty()) {
            // 과목명 조건: LIKE '%subject%'
            condition = condition.and(project.subjectName.containsIgnoreCase(searchProjectCond.subject()));
        }

        if(searchProjectCond.title() != null && !searchProjectCond.title().isEmpty()) {
            // 제목 조건: LIKE '%title%'
            condition = condition.and(project.title.containsIgnoreCase(searchProjectCond.title()));
        }

        // 프로젝트 목록 조회 쿼리
        JPAQuery<Project> query = queryFactory
                .select(project)
                .from(project)
                .leftJoin(project.user, user)
                .leftJoin(user.userDepartments, userDepartment) // User와 Department를 통해 매핑
                .leftJoin(project.projectFields, projectField)
                .leftJoin(userDepartment.department, department)
                .leftJoin(department.college, college)
                .leftJoin(project.semester, semester)
                .leftJoin(project.category, category)
                .where(condition)
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
                        p.getRepoName() != null,
                        new SearchSemesterResponse(
                                p.getSemester().getId(),
                                p.getSemester().getName()),
                        new SearchCategoryResponse(
                                p.getCategory().getId(),
                                p.getCategory().getName()),
                        p.getSubjectName(),
                        p.getLikeCount(),
                        p.getCommentCount(),
                        p.getIsPublic(),
                        p.getProjectFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                p.getUser().getId(),
                                p.getUser().getName(),
                                mapRoleToPosition(p.getUser().getRole())
                        ),
                        new SearchPatentSummaryResponse(
                                p.getProjectPatent() != null ? p.getProjectPatent().getId() : null,
                                p.getProjectPatent() != null && p.getProjectPatent().getAcceptAt() != null
                        )
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
