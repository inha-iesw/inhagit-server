package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.CreateGithubProjectRequest;
import inha.git.project.api.controller.dto.request.CreateProjectRequest;
import inha.git.project.api.controller.dto.request.UpdateProjectRequest;
import inha.git.project.api.controller.dto.response.ProjectResponse;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.project.api.controller.dto.response.SearchProjectResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.project.api.service.ProjectSearchService;
import inha.git.project.api.service.ProjectService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.file.ValidFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.*;

/**
 * ProjectController는 project 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project controller", description = "project 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectSearchService projectSearchService;



    /**
     * 프로젝트 전체 조회 API
     *
     * <p>프로젝트 전체를 조회합니다.</p>
     *
     * @param page 페이지 번호
     * @return 검색된 프로젝트 정보를 포함하는 BaseResponse<Page<SearchProjectsResponse>>
     */
    @GetMapping
    @Operation(summary = "프로젝트 전체 조회 API", description = "프로젝트 전체를 조회합니다.")
    public BaseResponse<Page<SearchProjectsResponse>> getProjects(@RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(PROJECT_SEARCH_OK, projectSearchService.getProjects(page - 1));
    }

    /**
     * 프로젝트 상세 조회 API
     *
     * <p>프로젝트 상세를 조회합니다.</p>
     *
     * @param projectIdx 프로젝트 ID
     * @return 프로젝트 상세 조회 결과를 포함하는 BaseResponse<SearchProjectResponse>
     */
    @GetMapping("/{projectIdx}")
    @Operation(summary = "프로젝트 상세 조회 API", description = "프로젝트 상세를 조회합니다.")
    public BaseResponse<SearchProjectResponse> getProject(
            @AuthenticationPrincipal User user,
            @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PROJECT_DETAIL_OK, projectSearchService.getProject(user ,projectIdx));
    }

    /**
     * 프로젝트 파일 조회 API
     *
     * <p>프로젝트 파일을 조회합니다.</p>
     *
     * @param projectIdx 프로젝트 ID
     * @param path       파일 경로
     * @return 프로젝트 파일 조회 결과를 포함하는 BaseResponse<List<SearchFileResponse>>
     */
    @GetMapping("/{projectIdx}/file")
    @Operation(summary = "프로젝트 파일 조회 API", description = "프로젝트 파일을 조회합니다.")
    public BaseResponse<List<SearchFileResponse>> getProjectFile(
            @PathVariable("projectIdx") Integer projectIdx,
            @RequestParam(value = "path", defaultValue = "/") String path) {
        return BaseResponse.of(FILE_SEARCH_OK, projectSearchService.getProjectFileByIdx(projectIdx, path));
    }

    /**
     * 프로젝트 생성
     *
     * @param user                사용자 정보
     * @param createProjectRequest 프로젝트 생성 요청
     * @param file                프로젝트 파일
     * @return 생성된 프로젝트 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로젝트 생성(기업 제외) API", description = "프로젝트를 생성합니다.")
    public BaseResponse<ProjectResponse> createProject(
            @AuthenticationPrincipal User user,
            @Validated @RequestPart("createProjectRequest") CreateProjectRequest createProjectRequest,
            @RequestPart(value = "file") MultipartFile file) {
        if (user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_PROJECT);
        }
        ValidFile.validateZipFile(file);
        return BaseResponse.of(PROJECT_CREATE_OK, projectService.createProject(user, createProjectRequest, file));
    }

    /**
     * GitHub 프로젝트 클론 및 압축 생성 API
     *
     * @param user                     사용자 정보
     * @param createGithubProjectRequest GitHub 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @PostMapping("/github")
    @Operation(summary = "GitHub 프로젝트 클론 및 압축 생성 API", description = "GitHub 레포지토리를 클론하고 압축하여 프로젝트를 생성합니다.")
    public BaseResponse<ProjectResponse> createGithubProject(@AuthenticationPrincipal User user,
                                                        @Validated @RequestBody CreateGithubProjectRequest createGithubProjectRequest) {
        if (user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_PROJECT);
        }
        return BaseResponse.of(PROJECT_CREATE_OK, projectService.cloneAndZipProject(user, createGithubProjectRequest));
    }

    @PutMapping("/github/{projectIdx}")
    @Operation(summary = "GitHub 프로젝트 수정 API", description = "GitHub 프로젝트를 수정합니다.")
    public BaseResponse<ProjectResponse> updateGithubProject(@AuthenticationPrincipal User user,
                                                            @PathVariable("projectIdx") Integer projectIdx) {
        if (user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_PROJECT);
        }
        return BaseResponse.of(PROJECT_UPDATE_OK, projectService.updateGithubProject(user, projectIdx));
    }

    /**
     * 프로젝트 수정
     *
     * @param user                사용자 정보
     * @param projectIdx          수정할 프로젝트 ID
     * @param updateProjectRequest 프로젝트 수정 요청
     * @param file                (선택 사항) 수정할 프로젝트 파일
     * @return 수정된 프로젝트 정보
     */
    @PutMapping(value = "/{projectIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로젝트 수정 API(기업 제외)", description = "프로젝트를 수정합니다.")
    public BaseResponse<ProjectResponse> updateProject(
            @AuthenticationPrincipal User user,
            @PathVariable("projectIdx") Integer projectIdx,
            @Validated @RequestPart("updateProjectRequest") UpdateProjectRequest updateProjectRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        if (file != null) {
            ValidFile.validateZipFile(file);
        }
        return BaseResponse.of(PROJECT_UPDATE_OK, projectService.updateProject(user, projectIdx, updateProjectRequest, file));
    }

    /**
     * 프로젝트 삭제
     *
     * @param user       사용자 정보
     * @param projectIdx 삭제할 프로젝트 ID
     * @return 삭제된 프로젝트 정보
     */
    @DeleteMapping("/{projectIdx}")
    @Operation(summary = "프로젝트 삭제 API", description = "프로젝트를 삭제합니다.")
    public BaseResponse<ProjectResponse> deleteProject(
            @AuthenticationPrincipal User user,
            @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PROJECT_DELETE_OK, projectService.deleteProject(user, projectIdx));
    }


}
