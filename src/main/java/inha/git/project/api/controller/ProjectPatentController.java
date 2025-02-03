package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.dto.request.CreatePatentRequest;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.service.patent.ProjectPatentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.SuccessStatus.*;

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
    public BaseResponse<SearchPatentResponse> getProjectPatent(@AuthenticationPrincipal User user,
                                                               @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PATENT_SEARCH_OK, projectPatentService.getProjectPatent(user, projectIdx));
    }

    /**
     * 특허 검색 API
     *
     * <p>특허 검색을 합니다.</p>
     *
     * @param user 사용자 정보
     * @param applicationNumber 특허 출원번호
     * @param projectIdx 프로젝트 인덱스
     * @return 검색된 특허 정보를 포함하는 BaseResponse<SearchPatentResponse>
     */
    @PostMapping("/{projectIdx}/search")
    @Operation(summary = "특허 검색 API", description = "특허 검색을 합니다.")
    public BaseResponse<SearchPatentResponse> searchProjectPatent(@AuthenticationPrincipal User user,
                                                                         @RequestParam("applicationNumber") String applicationNumber,
                                                                         @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PATENT_SEARCH_SUCCESS, projectPatentService.searchProjectPatent(user, applicationNumber, projectIdx));
    }

    /**
     * 특허 등록 API
     *
     * <p>특허를 등록합니다.</p>
     *
     * @param user 사용자 정보
     * @param applicationNumber 특허 출원번호
     * @param projectIdx 프로젝트 인덱스
     * @return 등록된 특허 정보를 포함하는 BaseResponse<PatentResponse>
     */
    @PostMapping("/{projectIdx}")
    @Operation(summary = "조회한 특허 등록 API", description = "조회한 특허를 등록합니다.")
    public BaseResponse<PatentResponse> registerPatent(@AuthenticationPrincipal User user,
                                                       @RequestParam("applicationNumber") String applicationNumber,
                                                       @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PATENT_REGISTER_SUCCESS, projectPatentService.registerPatent(user, applicationNumber, projectIdx));
    }

    /**
     * 특허 삭제 API
     *
     * <p>특허를 삭제합니다.</p>
     *
     * @param user 사용자 정보
     * @param projectIdx 프로젝트 인덱스
     * @return 삭제된 특허 정보를 포함하는 BaseResponse<PatentResponse>
     */
    @DeleteMapping("/{projectIdx}")
    @Operation(summary = "특허 삭제 API", description = "특허를 삭제합니다.")
    public BaseResponse<PatentResponse> deletePatent(@AuthenticationPrincipal User user,
                                           @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PATENT_DELETE_SUCCESS, projectPatentService.deletePatent(user, projectIdx));
    }

    /**
     * 직접 특허 등록 API
     *
     * <p>직접 특허를 등록합니다.</p>
     *
     * @param user 사용자 정보
     * @param projectIdx 프로젝트 인덱스
     * @param createPatentRequest 특허 등록 요청
     * @param file 특허 파일
     * @return 등록된 특허 정보를 포함하는 BaseResponse<PatentResponse>
     */
    @PostMapping("/{projectIdx}/manual")
    @Operation(summary = "직접 특허 등록 API", description = "직접 특허를 등록합니다.")
    public BaseResponse<PatentResponse> registerManualPatent(@AuthenticationPrincipal User user,
                                                             @PathVariable("projectIdx") Integer projectIdx,
                                                             @Validated @RequestPart CreatePatentRequest createPatentRequest,
                                                             @RequestPart("file") MultipartFile file) {
        return BaseResponse.of(PATENT_REGISTER_SUCCESS, projectPatentService.registerManualPatent(user, projectIdx, createPatentRequest, file));
    }
}
