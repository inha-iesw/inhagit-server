package inha.git.notice.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticesResponse;
import inha.git.notice.api.service.NoticeService;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static inha.git.common.code.status.SuccessStatus.*;

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
     * 공지 조회 API
     *
     * <p>공지를 조회.</p>
     *
     * @param page 페이지 번호
     *
     * @return 공지 조회 결과를 포함하는 BaseResponse<Page<SearchNoticeResponse>>
     */
    @GetMapping
    @Operation(summary = "공지 조회 API", description = "공지를 조회합니다.")
    public BaseResponse<Page<SearchNoticesResponse>> getNotices(@RequestParam("page") Integer page) {
        if (page < 1) {
            throw new BaseException(INVALID_PAGE);
        }
        return BaseResponse.of(NOTICE_SEARCH_OK, noticeService.getNotices(page - 1));
    }

    /**
     * 공지 상세 조회 API
     *
     * <p>공지를 상세 조회.</p>
     *
     * @param noticeIdx 공지 인덱스
     *
     * @return 공지 상세 조회 결과를 포함하는 BaseResponse<SearchNoticeResponse>
     */
    @GetMapping("/{noticeIdx}")
    @Operation(summary = "공지 상세 조회 API", description = "공지를 상세 조회합니다.")
    public BaseResponse<SearchNoticeResponse> getNotice(@PathVariable("noticeIdx") Integer noticeIdx) {
        return BaseResponse.of(NOTICE_DETAIL_OK, noticeService.getNotice(noticeIdx));
    }

    /**
     * 공지 생성 API
     *
     * <p>조교, 교수, 관리자만 호출 가능 -> 공지를 생성.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param createNoticeRequest 공지 생성 요청 정보
     * @param attachmentList 첨부파일 리스트
     *
     * @return 공지 생성 결과를 포함하는 BaseResponse<String>
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('assistant:create')")
    @Operation(summary = "공지 생성(조교, 교수, 관리자 전용) API", description = "공지를 생성합니다.")
    public BaseResponse<String> createNotice(@AuthenticationPrincipal User user,
                                             @Validated @RequestPart("createNoticeRequest") CreateNoticeRequest createNoticeRequest,
                                             @RequestPart(value = "attachmentList",required = false) List<MultipartFile> attachmentList) {
        log.info("공지 생성 - 사용자: {} 공지 제목: {}", user.getName(), createNoticeRequest.title());
        return BaseResponse.of(NOTICE_CREATE_OK, noticeService.createNotice(user, createNoticeRequest, attachmentList));
    }

    /**
     * 공지 수정 API
     *
     * <p>조교, 교수, 관리자만 호출 가능 -> 공지를 수정.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param noticeIdx 공지 인덱스
     * @param updateNoticeRequest 공지 수정 요청 정보
     * @param attachmentList 첨부파일 리스트
     *
     * @return 공지 수정 결과를 포함하는 BaseResponse<String>
     */
    @PutMapping(value = "/{noticeIdx}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('assistant:update')")
    @Operation(summary = "공지 수정(조교, 교수, 관리자 전용) API", description = "공지를 수정합니다.")
    public BaseResponse<String> updateNotice(@AuthenticationPrincipal User user,
                                             @PathVariable("noticeIdx") Integer noticeIdx,
                                             @Validated @RequestPart("updateNoticeRequest")  UpdateNoticeRequest updateNoticeRequest,
                                             @RequestPart(value = "attachmentList",required = false) List<MultipartFile> attachmentList) {
        log.info("공지 수정 - 사용자: {} 공지 제목: {}", user.getName(), updateNoticeRequest.title());
        return BaseResponse.of(NOTICE_UPDATE_OK, noticeService.updateNotice(user, noticeIdx, updateNoticeRequest, attachmentList));
    }

    /**
     * 공지 삭제 API
     *
     * <p>조교, 교수, 관리자만 호출 가능 -> 공지를 삭제.</p>
     *
     * @param user 로그인한 사용자 정보
     * @param noticeIdx 공지 인덱스
     *
     * @return 공지 삭제 결과를 포함하는 BaseResponse<String>
     */
    @DeleteMapping("/{noticeIdx}")
    @PreAuthorize("hasAuthority('assistant:delete')")
    @Operation(summary = "공지 삭제(조교, 교수, 관리자 전용) API", description = "공지를 삭제합니다.")
    public BaseResponse<String> deleteNotice(@AuthenticationPrincipal User user,
                                             @PathVariable("noticeIdx") Integer noticeIdx) {
        log.info("공지 삭제 - 사용자: {} 공지 인덱스: {}", user.getName(), noticeIdx);
        return BaseResponse.of(NOTICE_DELETE_OK, noticeService.deleteNotice(user, noticeIdx));
    }
}
