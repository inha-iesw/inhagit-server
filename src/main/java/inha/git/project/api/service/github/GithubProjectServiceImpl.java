package inha.git.project.api.service.github;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.repository.ProjectFieldJpaRepository;
import inha.git.project.api.controller.dto.request.CreateGithubProjectRequest;
import inha.git.project.api.controller.dto.response.ProjectResponse;
import inha.git.project.api.mapper.ProjectMapper;
import inha.git.project.domain.Project;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.user.domain.User;
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.CATEGORY_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;
import static inha.git.common.code.status.ErrorStatus.SEMESTER_NOT_FOUND;

/**
 * GithubProjectServiceImpl은 깃허브 프로젝트 관련 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GithubProjectServiceImpl implements GithubProjectService {

    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectFieldJpaRepository projectFieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;
    private final ProjectMapper projectMapper;
    private final StatisticsService statisticsService;
    private final IdempotentProvider idempotentProvider;

    /**
     * 깃허브 프로젝트 생성
     *
     * @param user                     사용자 정보
     * @param createGithubProjectRequest 깃허브 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @Override
    @Transactional
    public ProjectResponse createGithubProject(User user, CreateGithubProjectRequest createGithubProjectRequest) {
        idempotentProvider.isValidIdempotent(List.of("createGithubProject", user.getName(), user.getId().toString(), createGithubProjectRequest.title(), createGithubProjectRequest.contents(), createGithubProjectRequest.subject()));

        Semester semester = semesterJpaRepository.findByIdAndState(createGithubProjectRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));

        Category category = categoryJpaRepository.findById(createGithubProjectRequest.categoryIdx())
                .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));

        Project project = projectMapper.createGithubProjectRequestToProject(createGithubProjectRequest, user, semester, category);
        Project savedProject = projectJpaRepository.saveAndFlush(project);

        List<ProjectField> projectFields = createAndSaveProjectFields(createGithubProjectRequest.fieldIdxList(), savedProject);
        projectFieldJpaRepository.saveAll(projectFields);
        List<Field> fields = fieldJpaRepository.findAllById(createGithubProjectRequest.fieldIdxList());
        statisticsService.adjustCount(user, fields, semester,  category, 2, true);
        log.info("깃허브 프로젝트 생성 성공 - 사용자: {} 프로젝트 ID: {}", user.getName(), savedProject.getId());
        return projectMapper.projectToProjectResponse(savedProject);
    }

    private List<ProjectField>  createAndSaveProjectFields(List<Integer> fieldIdxList, Project project) {
        return fieldIdxList.stream()
                .map(fieldIdx -> {
                    Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    return projectMapper.createProjectField(project, field);
                }).toList();
    }
}
