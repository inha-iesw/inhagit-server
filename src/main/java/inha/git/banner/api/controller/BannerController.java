package inha.git.banner.api.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.ErrorStatus.BANNER_FILE_EMPTY;
import static inha.git.common.code.status.SuccessStatus.BANNER_CREATED_OK;

/**
 * BannerController는 banner 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "banner controller", description = "banner 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/banner")
public class BannerController {

    private final BannerService bannerService;
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
