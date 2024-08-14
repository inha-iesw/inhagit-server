package inha.git.banner.api.controller;

import inha.git.banner.api.controller.dto.response.BannerResponse;
import inha.git.banner.api.service.BannerService;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.BANNER_FILE_EMPTY;
import static inha.git.common.code.status.SuccessStatus.BANNER_CREATED_OK;
import static inha.git.common.code.status.SuccessStatus.BANNER_SEARCH_OK;

/**
 * BannerController는 banner 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "banner controller", description = "banner 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/banners")
public class BannerController {

    private final BannerService bannerService;

    /**
     * 배너 전체 조회
     *
     * @return 배너 목록
     */
    @GetMapping
    @Operation(summary = "배너 전체 조회", description = "배너 전체를 조회합니다.")
    public BaseResponse<List<BannerResponse>> getBanners() {
        return BaseResponse.of(BANNER_SEARCH_OK, bannerService.getBanners());
    }
    /**
     * 배너 생성
     *
     * @param user 사용자 정보
     * @param file 배너 파일
     * @return 생성된 배너 경로
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin:create')")
    @Operation(summary = "배너 생성(관리자 전용)", description = "배너를 생성합니다.")
    public BaseResponse<String> createBanner(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BaseException(BANNER_FILE_EMPTY);
        }
        return BaseResponse.of(BANNER_CREATED_OK,  bannerService.createBanner(user, file));
    }
}
