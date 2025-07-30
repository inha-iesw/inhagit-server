package inha.git.project.api.service.star;

import inha.git.admin.api.controller.dto.request.ProjectStarAcceptRequest;
import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import java.time.LocalDateTime;
import inha.git.project.api.controller.dto.response.*;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectStar;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectStarJpaRepository;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.PROJECT_STAR_SEARCH_PAGE_SUCCESS;
import static inha.git.utils.PagingUtils.toPageIndex;
import static inha.git.utils.PagingUtils.validatePage;

/**
 * ProjectStarServiceImpl은 프로젝트 Star 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProjectStarServiceImpl implements ProjectStarService {

    private final StatisticsService statisticsService;
    private final ProjectStarJpaRepository projectStarJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;

    @Override
    @Transactional(readOnly = true)
    public SearchProjectStarResponses searchProjectStar(User user, Integer projectIdx) {
        // 프로젝트 조회
        Project project = projectJpaRepository.findByIdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));

        // 프로젝트 Star 정보 확인
        ProjectStar projectStar = projectStarJpaRepository.findByProject_IdAndState(projectIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROJECT_STAR));

        if (projectStar.getAcceptAt() == null) {
            validateAuthorizationForUnacceptedStar(user, project);
        }

        SearchUserResponse searchUserResponse = new SearchUserResponse(
                project.getUser().getId(),
                project.getUser().getName(),
                mapRoleToPosition(project.getUser().getRole())
        );

        // 응답 매핑
        return projectMapper.toSearchProjectStarResponse(projectStar, searchUserResponse);
    }

    /**
     * Star 게시글 페이징 조회 메서드
     *
     * @param pageIndex 페이지 인덱스
     * @param size 페이지 사이즈
     * @return 특허 페이징 조회 결과
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SearchProjectStarResponses> searchProjectStarPage(Integer pageIndex, Integer size) {
        Pageable pageable = PageRequest.of(pageIndex, size);
        Page<ProjectStar> projectStarPage = projectStarJpaRepository.findByAcceptAtIsNotNullAndStateOrderByCreatedAtDesc(ACTIVE, pageable);

        return projectStarPage.map(projectStar -> {
            User user = projectStar.getProject().getUser();

            SearchUserResponse userResponse = new SearchUserResponse(
                    user.getId(),
                    user.getName(),
                    mapRoleToPosition(user.getRole())
            );

            Project project = projectStar.getProject();

            return new SearchProjectStarResponses(
                    projectStar.getId(),
                    project.getId(),
                    project.getTitle(),
                    project.getContents(),
                    project.getCreatedAt(),
                    project.getUpdatedAt(),
                    project.getRepoName() != null,
                    SearchSemesterResponse.from(project.getSemester()),
                    SearchCategoryResponse.from(project.getCategory()),
                    project.getSubjectName(),
                    project.getLikeCount(),
                    project.getCommentCount(),
                    project.getIsPublic(),
                    project.getProjectFields().stream()
                            .map(SearchFieldResponse::from)
                            .toList(),
                    SearchUserResponse.from(project.getUser()),
                    project.getProjectPatents().stream()
                            .map(SearchPatentSummaryResponse::from)
                            .toList(),
                    userResponse
            );
        });
    }

    /**
     * Star 게시글을 등록합니다.
     *
     * @param user 요청자 (관리자)
     * @param projectStarAcceptRequest 등록할 Star 정보
     * @return 등록된 Star 게시글 정보
     * @throws BaseException
     *         INVALID_USER_JWT : 관리자가 아닌 유저의 접근
     *         PROJECT_NOT_FOUND: 프로젝트를 찾을 수 없는 경우
     *         USER_NOT_PROJECT_OWNER: 등록 권한이 없는 경우
     *         ALREADY_REGISTERED_PATENT: 이미 등록된 특허가 있는 경우
     */
    @Override
    public ProjectStarResponses createProjectStar(User user, ProjectStarAcceptRequest projectStarAcceptRequest) {
        // 관리자 권한 확인
        if (user.getRole() != Role.ADMIN) {
            throw new BaseException(INVALID_USER_JWT);
        }
        // 프로젝트 조회
        Project project = projectJpaRepository.findByIdAndState(projectStarAcceptRequest.projectIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
        // 이미 등록된 Star가 있는지 확인
        boolean alreadyExists = projectStarJpaRepository
                .findByProject_IdAndState(projectStarAcceptRequest.projectIdx(), ACTIVE)
                .isPresent();

        if (alreadyExists) {
            throw new BaseException(ALREADY_REGISTERED_STAR);
        }

        ProjectStar projectStar = ProjectStar.builder()
                .project(project)
                .acceptAt(LocalDateTime.now())
                .state(ACTIVE)
                .build();

        ProjectStar saved = projectStarJpaRepository.save(projectStar);

        SearchUserResponse userResponse = new SearchUserResponse(
                user.getId(),
                user.getName(),
                mapRoleToPosition(user.getRole())
        );

        return new ProjectStarResponses(
                saved.getId(),
                saved.getProject().getId(),
                saved.getAcceptAt(),
                saved.getCreatedAt(),
                saved.getState(),
                userResponse
        );
    }

    /**
     * 프로젝트 Star 삭제 메서드
     *
     * @param user 요청자 (관리자)
     * @param starIdx 삭제할 Star ID
     * @return ProjectStarResponses
     */
    @Override
    public ProjectStarResponses deleteProjectStar(User user, Integer starIdx) {
        ProjectStar projectStar = projectStarJpaRepository.findByIdAndState(starIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(NOT_EXIST_PROJECT_STAR));

        Project project = projectStar.getProject();

        // 관리자만 삭제 가능
        if (user.getRole() != Role.ADMIN) {
            throw new BaseException(INVALID_USER_JWT);
        }

        // 상태만 INACTIVE로 변경 (Soft delete)
        projectStar.setState(INACTIVE);
        projectStar.setDeletedAt();

        SearchUserResponse userResponse = new SearchUserResponse(
                user.getId(),
                user.getName(),
                mapRoleToPosition(user.getRole())
        );


        return new ProjectStarResponses(
                projectStar.getId(),
                project.getId(),
                projectStar.getAcceptAt(),
                projectStar.getCreatedAt(),
                projectStar.getState(),
                userResponse
        );
    }

    private void validateAuthorizationForUnacceptedStar(User user, Project project) {
        boolean isOwner = project.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BaseException(INVALID_USER_JWT);
        }
    }

    private ProjectStarResponses buildResponse(ProjectStar projectStar, User user) {
        SearchUserResponse userResponse = new SearchUserResponse(
                user.getId(), user.getName(), mapRoleToPosition(user.getRole())
        );
        return new ProjectStarResponses(
                projectStar.getId(),
                projectStar.getProject().getId(),
                projectStar.getAcceptAt(),
                projectStar.getCreatedAt(),
                projectStar.getState(),
                userResponse
        );
    }
}
