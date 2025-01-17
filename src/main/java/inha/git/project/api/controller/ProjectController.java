package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.request.CreateGithubProjectRequest;
import inha.git.project.api.controller.dto.request.CreateProjectRequest;
import inha.git.project.api.controller.dto.request.SearchProjectCond;
import inha.git.project.api.controller.dto.request.UpdateProjectRequest;
import inha.git.project.api.controller.dto.response.ProjectResponse;
import inha.git.project.api.controller.dto.response.SearchFileResponse;
import inha.git.project.api.controller.dto.response.SearchProjectResponse;
import inha.git.project.api.controller.dto.response.SearchProjectsResponse;
import inha.git.project.api.service.github.GithubProjectService;
import inha.git.project.api.service.query.ProjectQueryService;
import inha.git.project.api.service.command.ProjectCommandService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.PagingUtils;
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

import static inha.git.common.code.status.ErrorStatus.COMPANY_CANNOT_CREATE_PROJECT;
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

    private final ProjectCommandService projectService;
    private final ProjectQueryService projectSearchService;
    private final GithubProjectService githubProjectService;

    /**
     * 프로젝트 조건 조회 API
     *
     * <p>프로젝트 조건에 맞게 조회합니다.</p>
     *
     * @param searchProjectCond 프로젝트 검색 조건
     * @param page              페이지 번호
     * @param size              size 페이지 사이즈
     * @return 검색된 프로젝트 정보를 포함하는 BaseResponse<Page<SearchProjectsResponse>>
     */
    @GetMapping("/cond")
    @Operation(summary = "프로젝트 조건 조회 API", description = "프로젝트 조건에 맞게 조회합니다.")
    public BaseResponse<Page<SearchProjectsResponse>> getCondProjects(@Validated @ModelAttribute SearchProjectCond searchProjectCond,
                                                                      @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        PagingUtils.validatePage(page, size);
        return BaseResponse.of(PROJECT_SEARCH_CONDITION_OK, projectSearchService.getCondProjects(searchProjectCond, PagingUtils.toPageIndex(page), size));
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
            @AuthenticationPrincipal User user,
            @PathVariable("projectIdx") Integer projectIdx,
            @RequestParam(value = "path", defaultValue = "/") String path) {
        return BaseResponse.of(FILE_SEARCH_OK, projectSearchService.getProjectFileByIdx(user, projectIdx, path));
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
            log.error("기업은 프로젝트를 생성할 수 없습니다. - 사용자: {}", user.getName());
            throw new BaseException(COMPANY_CANNOT_CREATE_PROJECT);
        }
        ValidFile.validateAndProcessZipFile(file);
        log.info("프로젝트 생성 - 사용자: {} 프로젝트 이름: {}", user.getName(), createProjectRequest.title());
        return BaseResponse.of(PROJECT_CREATE_OK, projectService.createProject(user, createProjectRequest, file));
    }

    /**
     * GitHub 프로젝트 생성
     *
     * @param user                        사용자 정보
     * @param createGithubProjectRequest GitHub 프로젝트 생성 요청
     * @return 생성된 프로젝트 정보
     */
    @PostMapping("/github")
    @Operation(summary = "GitHub 프로젝트 생성 API", description = "GitHub 프로젝트를 생성합니다.")
    public BaseResponse<ProjectResponse> createGithubProject(@AuthenticationPrincipal User user,
                                                        @Validated @RequestBody CreateGithubProjectRequest createGithubProjectRequest) {
        if (user.getRole() == Role.COMPANY) {
            log.error("기업은 프로젝트를 생성할 수 없습니다. - 사용자: {}", user.getName());
            throw new BaseException(COMPANY_CANNOT_CREATE_PROJECT);
        }
        log.info("GitHub 프로젝트 생성 - 사용자: {} 프로젝트 이름: {}", user.getName(), createGithubProjectRequest.title());
        return BaseResponse.of(PROJECT_CREATE_OK, githubProjectService.createGithubProject(user, createGithubProjectRequest));
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
            ValidFile.validateAndProcessZipFile(file);
        }
        log.info("프로젝트 수정 - 사용자: {} 프로젝트 이름: {}", user.getName(), updateProjectRequest.title());
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
        log.info("프로젝트 삭제 - 사용자: {} 프로젝트 ID: {}", user.getName(), projectIdx);
        return BaseResponse.of(PROJECT_DELETE_OK, projectService.deleteProject(user, projectIdx));
    }
}
