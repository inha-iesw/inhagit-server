package inha.git.notice.api.controller;

import inha.git.common.BaseResponse;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.service.NoticeService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.NOTICE_CREATE_OK;

/**
 * NoticeController는 notice 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "notice controller", description = "notice 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지 생성 API
     *
     * <p>조교, 교수, 관리자만 호출 가능 -> 공지를 생성.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param createNoticeRequest 공지 생성 요청 정보
     *
     * @return 공지 생성 결과를 포함하는 BaseResponse<String>
     */
    @PostMapping
    @PreAuthorize("hasAuthority('assistant:create')")
    @Operation(summary = "공지 생성(조교, 교수, 관리자 전용)", description = "공지를 생성합니다.")
    public BaseResponse<String> createNotice(@AuthenticationPrincipal User user,
                                             @Validated @RequestBody CreateNoticeRequest createNoticeRequest) {
        return BaseResponse.of(NOTICE_CREATE_OK, noticeService.createNotice(user, createNoticeRequest));
    }
}
