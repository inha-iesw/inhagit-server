package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.request.UpdatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.PatentResponses;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.service.patent.ProjectPatentService;
import inha.git.project.domain.enums.PatentType;
import inha.git.user.domain.User;
import inha.git.utils.PagingUtils;
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

import static inha.git.common.code.status.SuccessStatus.*;
import static inha.git.utils.PagingUtils.*;
import static inha.git.utils.PagingUtils.toPageIndex;

/**
 * ProjectPatentController는 project 특허 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project patent controller", description = "project patent 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/projects/patent")
public class ProjectPatentController {

    private final ProjectPatentService projectPatentService;

    /**
     * 특허 조회 API
     *
     * <p>특허를 조회합니다.</p>
     *
     * @param user 사용자 정보
     * @param projectIdx 프로젝트 인덱스
     * @return 조회된 특허 정보를 포함하는 BaseResponse<SearchPatentResponse>
     */
    @GetMapping("/{projectIdx}")
    @Operation(summary = "특허 조회 API", description = "특허를 조회합니다.")
    public BaseResponse<SearchPatentResponse> searchPatent(@AuthenticationPrincipal User user,
                                                           @PathVariable("projectIdx") Integer projectIdx,
                                                           @RequestParam("type") PatentType type) {
        return BaseResponse.of(PATENT_SEARCH_OK, projectPatentService.searchPatent(user, projectIdx, type));
    }

    /**
     * 특허 페이징 조회 API
     *
     * <p>특허를 페이징 조회합니다.</p>
     *
     * @param page 페이지
     * @param size 사이즈
     * @return 특허 페이징 조회 결과를 포함하는 BaseResponse<Page<PatentResponses>>
     */
    @GetMapping
    @Operation(summary = "특허 페이징 조회 API", description = "특허를 페이징 조회합니다.")
    public BaseResponse<Page<PatentResponses>> searchPatentPage(@RequestParam("page") Integer page,
                                                                @RequestParam("size") Integer size) {
        validatePage(page, size);
        return BaseResponse.of(PATENT_SEARCH_PAGE_SUCCESS, projectPatentService.searchPatentPage(toPageIndex(page), size));
    }

    /**
     * 특허 등록 API
     *
     * <p>특허를 등록합니다.</p>
     *
     * @param user 사용자 정보
     * @param createPatentRequest 특허 등록 요청
     * @param file 특허 파일
     * @return 등록된 특허 정보를 포함하는 BaseResponse<PatentResponse>
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "특허 등록 API", description = "특허를 등록합니다.")
    public BaseResponse<PatentResponse> createPatent(@AuthenticationPrincipal User user,
                                                             @Validated @RequestPart("createPatentRequest") CreatePatentRequest createPatentRequest,
                                                             @RequestPart(value = "file", required = false) MultipartFile file) {
        return BaseResponse.of(PATENT_REGISTER_SUCCESS, projectPatentService.createPatent(user, createPatentRequest, file));
    }

    /**
     * 특허 수정 API
     *
     * <p>특허를 수정합니다.</p>
     *
     * @param user 사용자 정보
     * @param patentIdx 특허 인덱스
     * @param updatePatentRequest 특허 수정 요청
     * @param file 특허 파일
     * @return 수정된 특허 정보를 포함하는 BaseResponse<PatentResponse>
     */
    @PutMapping(value = "/{patentIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "특허 수정 API", description = "특허를 수정합니다.")
    public BaseResponse<PatentResponse> updatePatent(@AuthenticationPrincipal User user,
                                                             @PathVariable("patentIdx") Integer patentIdx,
                                                             @Validated @RequestPart("updatePatentRequest") UpdatePatentRequest updatePatentRequest,
                                                             @RequestPart(value = "file", required = false) MultipartFile file) {
        return BaseResponse.of(PATENT_UPDATE_SUCCESS, projectPatentService.updatePatent(user, patentIdx, updatePatentRequest, file));
    }

    /**
     * 특허 삭제 API
     *
     * <p>특허를 삭제합니다.</p>
     *
     * @param user 사용자 정보
     * @param patentIdx 프로젝트 인덱스
     * @return 삭제된 특허 정보를 포함하는 BaseResponse<PatentResponse>
     */
    @DeleteMapping("/{patentIdx}")
    @Operation(summary = "특허 삭제 API", description = "특허를 삭제합니다.")
    public BaseResponse<PatentResponse> deletePatent(@AuthenticationPrincipal User user,
                                           @PathVariable("patentIdx") Integer patentIdx) {
        return BaseResponse.of(PATENT_DELETE_SUCCESS, projectPatentService.deletePatent(user, patentIdx));
    }
}
