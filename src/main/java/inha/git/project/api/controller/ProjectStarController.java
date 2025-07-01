package inha.git.project.api.controller;

import inha.git.common.BaseResponse;
//import inha.git.project.api.controller.dto.request.ProjectStarAcceptRequest;
import inha.git.project.api.controller.dto.response.*;
import inha.git.project.api.service.star.ProjectStarService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.SuccessStatus.*;
import static inha.git.utils.PagingUtils.toPageIndex;
import static inha.git.utils.PagingUtils.validatePage;

/**
 * ProjectStarController는 project Star 게시글 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project star controller", description = "project star 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/project/star")
public class ProjectStarController {

    private final ProjectStarService projectStarService;

    /**
     * 프로젝트 Star 조회 API
     *
     * <p>프로젝트 Star를 조회합니다.</p>
     *
     * @param user 사용자 정보
     * @param projectIdx 프로젝트 인덱스
     * @return 조회된 프로젝트 Star 정보를 포함하는 BaseResponse<SearchProjectStarResponse>
     */
    @GetMapping("/{projectIdx}")
    @Operation(summary = "프로젝트 Star 조회 API", description = "프로젝트 Star를 조회합니다.")
    public BaseResponse<SearchProjectStarResponses> searchProjectStar(@AuthenticationPrincipal User user,
                                                                 @PathVariable("projectIdx") Integer projectIdx) {
        return BaseResponse.of(PROJECT_STAR_SEARCH_OK, projectStarService.searchProjectStar(user, projectIdx));
    }

    /**
     * 프로젝트 Star 페이징 조회 API
     *
     * <p>프로젝트 Star를 페이징 조회합니다.</p>
     *
     * @param page 페이지
     * @param size 사이즈
     * @return 프로젝트 Star 페이징 조회 결과를 포함하는 BaseResponse<Page<ProjectStarResponses>>
     */
    @GetMapping
    @Operation(summary = "프로젝트 Star 페이징 조회 API", description = "프로젝트 Star를 페이징 조회합니다.")
    public BaseResponse<Page<SearchProjectStarResponses>> searchProjectStarPage(@RequestParam("page") Integer page,
                                                                @RequestParam("size") Integer size) {
        validatePage(page, size);
        return BaseResponse.of(PROJECT_STAR_SEARCH_PAGE_SUCCESS, projectStarService.searchProjectStarPage(toPageIndex(page), size));
    }
}
