package inha.git.notice.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticesResponse;
import inha.git.notice.api.service.NoticeService;
import inha.git.user.domain.User;
import inha.git.utils.PagingUtils;
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
 * 공지사항 관련 API를 처리하는 컨트롤러입니다.
 * 공지사항의 조회, 생성, 수정, 삭제 기능을 제공하며, 권한에 따른 접근 제어를 수행합니다.
 */
@Slf4j
@Tag(name = "notice controller", description = "notice 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;
    private final PagingUtils pagingUtils;

    /**
     * 전체 공지사항을 페이징하여 조회합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 페이지 번호와 크기의 유효성 검증<br>
     * 2. 페이지 정보를 인덱스로 변환<br>
     * 3. 페이징된 공지사항 목록 조회<br>
     * </p>
     *
     * @param page 조회할 페이지 번호 (1부터 시작)
     * @param size 페이지당 항목 수
     * @return 페이징된 공지사항 목록
     * @throws BaseException INVALID_PAGE: 페이지 번호가 유효하지 않은 경우
     *                      INVALID_SIZE: 페이지 크기가 유효하지 않은 경우
     */
    @GetMapping
    @Operation(summary = "공지 조회 API", description = "공지를 조회합니다.")
    public BaseResponse<Page<SearchNoticesResponse>> getNotices(@RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        pagingUtils.validatePage(page);
        pagingUtils.validateSize(size);
        return BaseResponse.of(NOTICE_SEARCH_OK, noticeService.getNotices(pagingUtils.toPageIndex(page), pagingUtils.toPageSize(size)));
    }

    /**
     * 특정 공지사항의 상세 정보를 조회합니다.
     *
     * <p>
     * 공지사항의 제목, 내용, 작성자 정보, 첨부파일 정보를 포함한 상세 정보를 조회합니다.
     * </p>
     *
     * @param noticeIdx 조회할 공지사항의 식별자
     * @return 공지사항 상세 정보
     * @throws BaseException NOTICE_NOT_FOUND: 공지사항을 찾을 수 없는 경우
     */
    @GetMapping("/{noticeIdx}")
    @Operation(summary = "공지 상세 조회 API", description = "공지를 상세 조회합니다.")
    public BaseResponse<SearchNoticeResponse> getNotice(@PathVariable("noticeIdx") Integer noticeIdx) {
        return BaseResponse.of(NOTICE_DETAIL_OK, noticeService.getNotice(noticeIdx));
    }

    /**
     * 새로운 공지사항을 생성합니다.
     * 조교, 교수, 관리자 권한을 가진 사용자만 접근 가능합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 권한 검증<br>
     * 2. 공지사항 정보 저장<br>
     * 3. 첨부파일이 있는 경우 파일 업로드 및 정보 저장<br>
     * </p>
     *
     * @param user 현재 인증된 사용자 정보
     * @param createNoticeRequest 생성할 공지사항 정보 (제목, 내용)
     * @param attachmentList 첨부파일 목록 (선택적)
     * @return 공지사항 생성 결과 메시지
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
     * 기존 공지사항을 수정합니다.
     * 조교, 교수, 관리자 권한을 가진 사용자만 접근 가능하며,
     * 관리자가 아닌 경우 본인이 작성한 공지사항만 수정할 수 있습니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 권한 검증<br>
     * 2. 공지사항 정보 수정<br>
     * 3. 첨부파일 수정 (기존 파일 삭제 및 새로운 파일 업로드)<br>
     * </p>
     *
     * @param user 현재 인증된 사용자 정보
     * @param noticeIdx 수정할 공지사항의 식별자
     * @param updateNoticeRequest 수정할 내용 (제목, 내용)
     * @param attachmentList 새로운 첨부파일 목록 (선택적)
     * @return 공지사항 수정 결과 메시지
     * @throws BaseException NOTICE_NOT_FOUND: 공지사항을 찾을 수 없는 경우
     *                      NOTICE_NOT_AUTHORIZED: 수정 권한이 없는 경우
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
     * 공지사항을 삭제(비활성화) 처리합니다.
     * 조교, 교수, 관리자 권한을 가진 사용자만 접근 가능하며,
     * 관리자가 아닌 경우 본인이 작성한 공지사항만 삭제할 수 있습니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 권한 검증<br>
     * 2. 공지사항 상태를 INACTIVE로 변경<br>
     * 3. 삭제 시간 기록<br>
     * </p>
     *
     * @param user 현재 인증된 사용자 정보
     * @param noticeIdx 삭제할 공지사항의 식별자
     * @return 공지사항 삭제 결과 메시지
     * @throws BaseException NOTICE_NOT_FOUND: 공지사항을 찾을 수 없는 경우
     *                      NOTICE_NOT_AUTHORIZED: 삭제 권한이 없는 경우
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
