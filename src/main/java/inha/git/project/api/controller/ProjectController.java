package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.request.UpdateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.project.api.controller.api.response.UpdateProjectResponse;
import inha.git.project.api.service.ProjectService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.file.ValidFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.PROJECT_CREATE_OK;
import static inha.git.common.code.status.SuccessStatus.PROJECT_UPDATE_OK;

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

    /**
     * 프로젝트 생성
     *
     * @param user                사용자 정보
     * @param createProjectRequest 프로젝트 생성 요청
     * @param file                프로젝트 파일
     * @return 생성된 프로젝트 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로젝트 생성 API", description = "프로젝트를 생성합니다.")
    public BaseResponse<CreateProjectResponse> createProject(
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
     * 프로젝트 수정
     *
     * @param user                사용자 정보
     * @param projectIdx          수정할 프로젝트 ID
     * @param updateProjectRequest 프로젝트 수정 요청
     * @param file                (선택 사항) 수정할 프로젝트 파일
     * @return 수정된 프로젝트 정보
     */
    @PutMapping(value = "/{projectIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "프로젝트 수정 API", description = "프로젝트를 수정합니다.")
    public BaseResponse<UpdateProjectResponse> updateProject(
            @AuthenticationPrincipal User user,
            @PathVariable("projectIdx") Integer projectIdx,
            @Validated @RequestPart("updateProjectRequest") UpdateProjectRequest updateProjectRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        if (user.getRole() == Role.COMPANY) {
            throw new BaseException(COMPANY_CANNOT_CREATE_PROJECT);
        }
        if (file != null) {
            ValidFile.validateZipFile(file);
        }
        return BaseResponse.of(PROJECT_UPDATE_OK, projectService.updateProject(user, projectIdx, updateProjectRequest, file));
    }
}
