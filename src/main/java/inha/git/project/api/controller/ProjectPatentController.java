package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
import inha.git.project.api.controller.dto.response.PatentResponse;
import inha.git.project.api.controller.dto.response.SearchPatentResponse;
import inha.git.project.api.service.ProjectPatentService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static inha.git.common.code.status.SuccessStatus.PATENT_REGISTER_SUCCESS;
import static inha.git.common.code.status.SuccessStatus.PATENT_SEARCH_OK;

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


    @PostMapping("/{projectIdx}/search")
    @Operation(summary = "특허 검색 API", description = "특허 검색을 합니다.")
    public BaseResponse<SearchPatentResponse> getPatentRecommendProjects(@AuthenticationPrincipal User user,
                                                                         @RequestParam("applicationNumber") String applicationNumber,
                                                                         @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PATENT_SEARCH_OK, projectPatentService.getPatent(user, applicationNumber, projectIdx));
    }

    // 조회한 특허 등록
    @PostMapping
    @Operation(summary = "조회한 특허 등록 API", description = "조회한 특허를 등록합니다.")
    public BaseResponse<PatentResponse> registerPatent(@AuthenticationPrincipal User user,
                                                       @RequestParam("applicationNumber") String applicationNumber) {
        return BaseResponse.of(PATENT_REGISTER_SUCCESS, projectPatentService.registerPatent(user, applicationNumber));
    }


}
