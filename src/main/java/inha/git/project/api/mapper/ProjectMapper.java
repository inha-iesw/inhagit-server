package inha.git.project.api.mapper;

import inha.git.field.domain.Field;
import inha.git.mapping.domain.*;
import inha.git.mapping.domain.id.*;
import inha.git.project.api.controller.dto.request.*;
import inha.git.project.api.controller.dto.response.*;
import inha.git.project.domain.*;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.ArrayList;
import java.util.List;

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
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "foundingRecommendCount", constant = "0")
    @Mapping(target = "registrationRecommendCount", constant = "0")
    @Mapping(target = "commentCount", constant = "0")
    @Mapping(target = "subjectName", source = "createProjectRequest.subject")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "semester", source = "semester")
    @Mapping(target = "id", ignore = true)
    Project createProjectRequestToProject(CreateProjectRequest createProjectRequest, User user, Semester semester);
    /**
     * UpdateProjectRequest를 Project 엔티티로 변환
     *
     * @param updateProjectRequest 프로젝트 업데이트 요청
     * @param project              프로젝트 엔티티
     */
    @Mapping(target = "subjectName", source = "updateProjectRequest.subject")
    @Mapping(target = "title", source = "updateProjectRequest.title")
    @Mapping(target = "contents", source = "updateProjectRequest.contents")
    @Mapping(target = "semester", source = "semester")
    void updateProjectRequestToProject(UpdateProjectRequest updateProjectRequest, @MappingTarget Project project, Semester semester);

    /**
     * Project 엔티티를 ProjectResponse로 변환
     *
     * @param project 프로젝트 엔티티
     * @return ProjectResponse
     */
    @Mapping(target = "idx", source = "project.id")
    ProjectResponse projectToProjectResponse(Project project);


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

    /**
     * Field 엔티티를 SearchFieldResponse로 변환
     *
     * @param field 필드 엔티티
     * @return SearchFieldResponse
     */
    @Mapping(target = "idx", source = "field.id")
    @Mapping(target = "name", source = "field.name")
    SearchFieldResponse projectFieldToSearchFieldResponse(Field field);

    /**
     * Project 엔티티를 SearchProjectResponse로 변환
     *
     * @param project 프로젝트 엔티티
     * @return SearchProjectResponse
     */
    @Mapping(target = "like", source = "likeCount")
    @Mapping(target = "founding", source = "foundingRecommendCount")
    @Mapping(target = "registration", source = "registrationRecommendCount")
    SearchRecommendCount projectToSearchRecommendCountResponse(Project project);

    /**
     * User 엔티티를 SearchUserResponse로 변환
     *
     * @param user 사용자 엔티티
     * @return SearchUserResponse
     */
    @Mapping(target = "idx", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    SearchUserResponse userToSearchUserResponse(User user);

    /**
     * Boolean 값을 SearchRecommendState로 변환
     *
     * @param isLike      좋아요 여부
     * @param isRecommendFounding    창업 추천 여부
     * @param isRecommendRegistration 등록 추천 여부
     * @return SearchRecommendState
     */
    @Mapping(target = "like", source = "isLike")
    @Mapping(target = "founding", source = "isRecommendFounding")
    @Mapping(target = "registration", source = "isRecommendRegistration")
    SearchRecommendState projectToSearchRecommendState(Boolean isLike, Boolean isRecommendFounding, Boolean isRecommendRegistration);

    /**
     * Project 엔티티를 SearchProjectResponse로 변환
     *
     * @param project         프로젝트 엔티티
     * @param projectUpload   프로젝트 업로드 엔티티
     * @param fieldList       필드 리스트
     * @param recommendCount  추천 수
     * @param author          작성자 정보
     * @param recommendState  추천 상태
     * @param semester         학기 정보
     * @return SearchProjectResponse
     */
    @Mapping(target = "idx", source = "project.id")
    @Mapping(target = "subject", source = "project.subjectName")
    @Mapping(target = "filePath", source = "projectUpload.directoryName")
    @Mapping(target = "zipFilePath", source = "projectUpload.zipDirectoryName")
    @Mapping(target = "repoName", source = "project.repoName")
    @Mapping(target = "createdAt", source = "project.createdAt")
    @Mapping(target = "semester", source = "semester")
    SearchProjectResponse projectToSearchProjectResponse(Project project, ProjectUpload projectUpload, List<SearchFieldResponse> fieldList, SearchRecommendCount recommendCount, SearchUserResponse author, SearchRecommendState recommendState, SearchSemesterResponse semester);

    /**
     * FoundingRecommend 엔티티 생성
     *
     * @param user    사용자 정보
     * @param project 프로젝트 정보
     * @return 창업 추천 엔티티
     */
    default FoundingRecommend createProjectFoundingRecommend(User user, Project project) {
        return new FoundingRecommend(new FoundingRecommendId(user.getId(), project.getId()), project, user);
    }

    /**
     * 좋아요 엔티티 생성
     *
     * @param user    사용자 정보
     * @param project 프로젝트 정보
     * @return 특허 추천 엔티티
     */
    default ProjectLike createProjectLike(User user, Project project) {
        return new ProjectLike(new ProjectLikeId(user.getId(), project.getId()), project, user);
    }

    /**
     * 등록 추천 엔티티 생성
     *
     * @param user    사용자 정보
     * @param project 프로젝트 정보
     * @return 등록 추천 엔티티
     */
    default RegistrationRecommend createProjectRegistrationRecommend(User user, Project project) {
        return new RegistrationRecommend(new RegistrationRecommendId(user.getId(), project.getId()), project, user);
    }

    /**
     * ProjectComment 엔티티 생성
     *
     * @param createCommentRequest 댓글 생성 요청
     * @param user                 사용자 정보
     * @param project              프로젝트 정보
     * @return ProjectComment 엔티티
     */
    @Mapping(target = "contents", source = "createCommentRequest.contents")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "project", source = "project")
    ProjectComment toProjectComment(CreateCommentRequest createCommentRequest, User user, Project project);

    /**
     * ProjectComment 엔티티를 CommentResponse로 변환
     *
     * @param projectComment 프로젝트 댓글 엔티티
     * @return CommentResponse
     */
    @Mapping(target = "idx", source = "projectComment.id")
    CommentResponse toCommentResponse(ProjectComment projectComment);

    /**
     * ProjectReplyComment 엔티티 생성
     *
     * @param createReplyCommentRequest 답글 생성 요청
     * @param user                      사용자 정보
     * @param projectComment            프로젝트 댓글 정보
     * @return ProjectReplyComment 엔티티
     */
    @Mapping(target = "contents", source = "createReplyCommentRequest.contents")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "projectComment", source = "projectComment")
    ProjectReplyComment toProjectReplyComment(CreateReplyCommentRequest createReplyCommentRequest, User user, ProjectComment projectComment);

    /**
     * ProjectReplyComment 엔티티를 ReplyCommentResponse로 변환
     *
     * @param projectReplyComment 프로젝트 답글 엔티티
     * @return ReplyCommentResponse
     */
    @Mapping(target = "idx", source = "projectReplyComment.id")
    ReplyCommentResponse toReplyCommentResponse(ProjectReplyComment projectReplyComment);


    /**
     * ProjectComment 엔티티를 CommentWithRepliesResponse로 변환
     *
     * @param projectComment 프로젝트 댓글 엔티티
     * @param likeState 좋아요 상태
     * @param replies 답글
     * @return CommentWithRepliesResponse
     */
    @Mapping(target = "idx", source = "projectComment.id")
    @Mapping(target = "author", source = "projectComment.user")
    @Mapping(target = "replies", source = "replies")  // 대댓글 리스트는 이미 처리된 상태로 전달됨
    CommentWithRepliesResponse toCommentWithRepliesResponse(ProjectComment projectComment, Boolean likeState, List<SearchReplyCommentResponse> replies);

    /**
     * ProjectReplyComment 엔티티를 SearchReplyCommentResponse로 변환
     *
     * @param projectReplyComment 프로젝트 답글 엔티티
     * @param likeState 좋아요 상태
     * @return SearchReplyCommentResponse
     */
    @Mapping(target = "likeState", source = "likeState")
    @Mapping(target = "idx", source = "projectReplyComment.id")
    @Mapping(target = "author", source = "projectReplyComment.user")
    SearchReplyCommentResponse toSearchReplyCommentResponse(ProjectReplyComment projectReplyComment, boolean likeState);

    /**
     * ProjectReplyComment 엔티티를 SearchReplyCommentResponse로 변환
     *
     * @param projectReplyComment 프로젝트 답글 엔티티
     * @return SearchReplyCommentResponse
     */
    @Mapping(target = "idx", source = "projectReplyComment.id")
    @Mapping(target = "contents", source = "projectReplyComment.contents")
    @Mapping(target = "author", source = "projectReplyComment.user")
    @Mapping(target = "likeCount", source = "projectReplyComment.likeCount")
    @Mapping(target = "createdAt", source = "projectReplyComment.createdAt")
    SearchReplyCommentResponse toSearchReplyCommentResponse(ProjectReplyComment projectReplyComment);


    /**
     * CreateGithubProjectRequest를 Project 엔티티로 변환
     *
     * @param createGithubProjectRequest 깃허브 프로젝트 생성 요청
     * @param user                       사용자 정보
     * @return Project 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "foundingRecommendCount", constant = "0")
    @Mapping(target = "registrationRecommendCount", constant = "0")
    @Mapping(target = "commentCount", constant = "0")
    @Mapping(target = "subjectName", source = "createGithubProjectRequest.subject")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "semester", source = "semester")
    Project createGithubProjectRequestToProject(CreateGithubProjectRequest createGithubProjectRequest, User user, Semester semester);


    SearchPatentResponse toSearchPatentResponse(String applicationNumber, String applicationDate, String inventionTitle,
                                                String inventionTitleEnglish, String applicantName, String applicantEnglishName,
                                                List<SearchInventorResponse> inventors);
    @Mapping(target = "applicationDate", source = "applicationDate")
    @Mapping(target = "inventionTitle", source = "inventionTitle")
    @Mapping(target = "inventionTitleEnglish", source = "inventionTitleEng")
    SearchPatentResponse toSearchPatentResponse(String applicationDate, String inventionTitle, String inventionTitleEng);

    @Mapping(target = "applicantName", source = "applicantName")
    @Mapping(target = "applicantEnglishName", source = "applicantEnglishName")
    SearchPatentResponse toSearchPatentResponse(String applicantName, String applicantEnglishName);


    default List<ProjectPatentInventor> toPatentInventor(List<SearchInventorResponse> inventors, ProjectPatent projectPatent) {
        List<ProjectPatentInventor> result = new ArrayList<>();
        for (SearchInventorResponse inventor : inventors) {
            result.add(toPatentInventor(inventor, projectPatent));
        }
        return result;
    }

    @Mapping(target ="id", ignore = true)
    ProjectPatentInventor toPatentInventor(SearchInventorResponse inventor, ProjectPatent projectPatent);

    @Mapping(target = "applicationNumber", source = "projectPatent.applicationNumber")
    @Mapping(target = "applicationDate", source = "projectPatent.applicationDate")
    @Mapping(target = "inventionTitle", source = "projectPatent.inventionTitle")
    @Mapping(target = "inventionTitleEnglish", source = "projectPatent.inventionTitleEnglish")
    @Mapping(target = "applicantName", source = "projectPatent.applicantName")
    @Mapping(target = "applicantEnglishName", source = "projectPatent.applicantEnglishName")
    @Mapping(target = "inventors", source = "patentInventors")
    SearchPatentResponse toSearchPatentResponse(ProjectPatent projectPatent, List<ProjectPatentInventor> patentInventors);

    @Mapping(target = "id", ignore = true)
    ProjectPatent toProjectPatent(String applicationNumber, String applicationDate, String inventionTitle, String inventionTitleEnglish, String applicantName, String applicantEnglishName);

    @Mapping(target = "idx", source = "projectPatent.id")
    PatentResponse toPatentResponse(ProjectPatent projectPatent);

    default ProjectCommentLike createProjectCommentLike(User user, ProjectComment projectComment) {
        return new ProjectCommentLike(new ProjectCommentLikeId(user.getId(), projectComment.getId()), projectComment, user);
    }

    default ProjectReplyCommentLike createProjectReplyCommentLike(User user, ProjectReplyComment projectReplyComment) {
        return new ProjectReplyCommentLike(new ProjectReplyCommentLikeId(user.getId(), projectReplyComment.getId()), projectReplyComment, user);
    }

}
