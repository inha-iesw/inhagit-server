package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.api.request.CreateProjectRequest;
import inha.git.project.api.controller.api.response.CreateProjectResponse;
import inha.git.project.api.service.ProjectService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.PROJECT_CREATE_OK;

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
        validateZipFile(file);
        return BaseResponse.of(PROJECT_CREATE_OK, projectService.createProject(user, createProjectRequest, file));
    }




    private static final int MAX_FILES = 100;
    private static final long MAX_SIZE_MB = 200;
    private static final long MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024;
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>();

    static {
        ALLOWED_CONTENT_TYPES.add("application/zip");
        ALLOWED_CONTENT_TYPES.add("application/octet-stream");
        ALLOWED_CONTENT_TYPES.add("multipart/x-zip");
        ALLOWED_CONTENT_TYPES.add("application/zip-compressed");
        ALLOWED_CONTENT_TYPES.add("application/x-zip-compressed");
        ALLOWED_CONTENT_TYPES.add("application/x-zip");
    }

    /**
     * zip 파일 유효성 검사
     *
     * @param file zip 파일
     */
    private void validateZipFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BaseException(FILE_NOT_FOUND);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BaseException(FILE_NOT_ZIP);
        }

        try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
            ZipEntry entry;
            int fileCount = 0;
            long totalSize = 0;

            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    fileCount++;
                    totalSize += entry.getSize();

                    if (fileCount > MAX_FILES) {
                        throw new BaseException(FILE_MAX_FILES);
                    }

                    if (totalSize > MAX_SIZE_BYTES) {
                        throw new BaseException(FILE_MAX_SIZE);
                    }
                }
            }
        } catch (IOException e) {
            throw new BaseException(FILE_CONVERT);
        }
    }

}
