package inha.git.image.api.controller;

import inha.git.common.BaseResponse;
import inha.git.user.domain.User;
import inha.git.image.api.controller.dto.response.ImageResponse;
import inha.git.image.api.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static inha.git.common.code.status.SuccessStatus.IMAGE_CREATE_OK;

/**
 * ImageController는 이미지 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "image controller", description = "image 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    /**
     * 이미지 생성 API
     *
     * @param user  유저 정보
     * @param image 이미지 파일
     * @return 생성된 이미지 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "이미지 생성 API", description = "이미지를 생성합니다.")
    public BaseResponse<ImageResponse> createBanner(
            @AuthenticationPrincipal User user,
            @RequestPart("image") MultipartFile image) {
        log.info("이미지 생성 - 유저: {}", user.getName());
        return BaseResponse.of(IMAGE_CREATE_OK, imageService.createImage(user, image));
    }
}
