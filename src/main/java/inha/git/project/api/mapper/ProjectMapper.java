package inha.git.project.api.mapper;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectUpload;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * ProjectMapper는 Project 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

    /**
     * CreateProjectRequest를 Project 엔티티로 변환
     *
     * @param createProjectRequest 프로젝트 생성 요청
     * @param user                 사용자 정보
     * @return Project 엔티티
     */
    @Mapping(target = "patentRecommendCount", constant = "0")
    @Mapping(target = "foundingRecommendCount", constant = "0")
    @Mapping(target = "registrationRecommendCount", constant = "0")
    @Mapping(target = "subjectName", source = "createProjectRequest.subject")
    @Mapping(target = "user", source = "user")
    Project createProjectRequestToProject(CreateProjectRequest createProjectRequest, User user);

    /**
     * Project 엔티티를 CreateProjectResponse로 변환
     *
     * @param project 프로젝트 정보
     * @return CreateProjectResponse
     */
    @Mapping(target = "contents", source = "contents")
    @Mapping(target = "directoryName", source = "directoryName")
    @Mapping(target = "zipDirectoryName", source = "zipDirectoryName")
    @Mapping(target = "project", source = "project")
    ProjectUpload createProjectUpload(String contents, String directoryName, String zipDirectoryName, Project project);

    /**
     * Project 엔티티를 ProjectField 엔티티로 변환
     *
     * @param project 프로젝트 정보
     * @param field   분야 정보
     * @return ProjectField 엔티티
     */
    default ProjectField createProjectField(Project project, Field field) {
        return new ProjectField(new ProjectFieldId(project.getId(), field.getId()), project, field);
    }

    /**
     * Project 엔티티를 CreateProjectResponse로 변환
     *
     * @param project 프로젝트 정보
     * @return CreateProjectResponse
     */
    @Mapping(target = "idx", source = "project.id")
    CreateProjectResponse projectToCreateProjectResponse(Project project);
}
