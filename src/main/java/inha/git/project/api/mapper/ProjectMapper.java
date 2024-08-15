package inha.git.project.api.mapper;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.ProjectField;
import inha.git.mapping.domain.id.ProjectFieldId;
import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.request.UpdateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.project.api.controller.api.response.UpdateProjectResponse;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectUpload;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
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
     * UpdateProjectRequest를 Project 엔티티로 변환
     *
     * @param updateProjectRequest 프로젝트 업데이트 요청
     * @param project              프로젝트 엔티티
     */
    @Mapping(target = "subjectName", source = "updateProjectRequest.subject")
    @Mapping(target = "title", source = "updateProjectRequest.title")
    @Mapping(target = "contents", source = "updateProjectRequest.contents")
    void updateProjectRequestToProject(UpdateProjectRequest updateProjectRequest, @MappingTarget Project project);

    /**
     * Project 엔티티를 CreateProjectResponse로 변환
     *
     * @param project 프로젝트 엔티티
     * @return CreateProjectResponse
     */
    @Mapping(target = "idx", source = "project.id")
    CreateProjectResponse projectToCreateProjectResponse(Project project);


    /**
     * Project 엔티티를 UpdateProjectResponse로 변환
     *
     * @param project 프로젝트 엔티티
     * @return UpdateProjectResponse
     */
    @Mapping(target = "idx", source = "project.id")
    UpdateProjectResponse projectToUpdateProjectResponse(Project project);

    /**
     * Project 엔티티를 UpdateProjectResponse로 변환
     *
     * @param project 프로젝트 엔티티
     * @return UpdateProjectResponse
     */
    @Mapping(target = "directoryName", source = "directoryName")
    @Mapping(target = "zipDirectoryName", source = "zipDirectoryName")
    @Mapping(target = "project", source = "project")
    ProjectUpload createProjectUpload(String directoryName, String zipDirectoryName, Project project);

    /**
     * ProjectUpload 엔티티를 업데이트
     *
     * @param directoryName    디렉토리 이름
     * @param zipDirectoryName  zip 디렉토리 이름
     * @param projectUpload    프로젝트 업로드 엔티티
     */
    @Mapping(target = "directoryName", source = "directoryName")
    @Mapping(target = "zipDirectoryName", source = "zipDirectoryName")
    void updateProjectUpload(String directoryName, String zipDirectoryName, @MappingTarget ProjectUpload projectUpload);

    /**
     * ProjectField 엔티티 생성
     *
     * @param project 프로젝트 엔티티
     * @param field   필드 엔티티
     * @return ProjectField 엔티티
     */
    default ProjectField createProjectField(Project project, Field field) {
        return new ProjectField(new ProjectFieldId(project.getId(), field.getId()), project, field);
    }
}
