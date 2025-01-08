package inha.git.notice.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeAttachmentResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticeUserResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticesResponse;
import inha.git.notice.api.service.NoticeService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.PagingUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@DisplayName("공지사항 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class NoticeControllerTest {

    @InjectMocks
    private NoticeController noticeController;

    @Mock
    private NoticeService noticeService;

    @Mock
    private PagingUtils pagingUtils;

    @Test
    @DisplayName("공지사항 페이징 조회 성공")
    void getNotices_Success() {
        // given
        Integer page = 1;
        Integer size = 10;
        int pageIndex = 0;
        int pageSize = 9;
        Page<SearchNoticesResponse> expectedPage = new PageImpl<>(Arrays.asList(
                new SearchNoticesResponse(1, "공지1", LocalDateTime.now(), false, new SearchNoticeUserResponse(1, "작성자1")),
                new SearchNoticesResponse(2, "공지2", LocalDateTime.now(), false, new SearchNoticeUserResponse(2, "작성자2"))
        ));

        given(pagingUtils.toPageIndex(page)).willReturn(pageIndex);
        given(pagingUtils.toPageSize(size)).willReturn(pageSize);
        given(noticeService.getNotices(pageIndex, pageSize)).willReturn(expectedPage);

        // when
        BaseResponse<Page<SearchNoticesResponse>> response = noticeController.getNotices(page, size);

        // then
        assertThat(response.getResult()).isEqualTo(expectedPage);
        verify(pagingUtils).validatePage(page);
        verify(pagingUtils).validateSize(size);
        verify(noticeService).getNotices(pageIndex, pageSize);
    }

    @Test
    @DisplayName("잘못된 페이지 번호로 조회 시 예외 발생")
    void getNotices_WithInvalidPage_ThrowsException() {
        // given
        Integer invalidPage = 0;
        Integer size = 10;

        doThrow(new BaseException(INVALID_PAGE))
                .when(pagingUtils).validatePage(invalidPage);

        // when & then
        assertThatThrownBy(() -> noticeController.getNotices(invalidPage, size))
                .isInstanceOf(BaseException.class)
                .hasMessage(INVALID_PAGE.getMessage());
    }

    @Test
    @DisplayName("공지사항 상세 조회 성공")
    void getNotice_Success() {
        // given
        Integer noticeIdx = 1;
        SearchNoticeResponse expectedResponse = createSearchNoticeResponse(noticeIdx);

        given(noticeService.getNotice(noticeIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<SearchNoticeResponse> response = noticeController.getNotice(noticeIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(noticeService).getNotice(noticeIdx);
    }

    @Test
    @DisplayName("공지사항 생성 성공")
    void createNotice_Success() {
        // given
        User user = createUser(1, "작성자", Role.ASSISTANT);
        CreateNoticeRequest request = new CreateNoticeRequest("제목", "내용");
        List<MultipartFile> attachments = new ArrayList<>();
        String expectedResponse = "제목 공지가 생성되었습니다.";

        given(noticeService.createNotice(user, request, attachments))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = noticeController.createNotice(user, request, attachments);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(noticeService).createNotice(user, request, attachments);
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void updateNotice_Success() {
        // given
        User user = createUser(1, "작성자", Role.ASSISTANT);
        Integer noticeIdx = 1;
        UpdateNoticeRequest request = new UpdateNoticeRequest("수정된제목", "수정된내용");
        List<MultipartFile> attachments = new ArrayList<>();
        String expectedResponse = "수정된제목 공지가 수정되었습니다.";

        given(noticeService.updateNotice(user, noticeIdx, request, attachments))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = noticeController.updateNotice(user, noticeIdx, request, attachments);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(noticeService).updateNotice(user, noticeIdx, request, attachments);
    }

    @Test
    @DisplayName("공지사항 삭제 성공")
    void deleteNotice_Success() {
        // given
        User user = createUser(1, "작성자", Role.ASSISTANT);
        Integer noticeIdx = 1;
        String expectedResponse = "공지가 삭제되었습니다.";

        given(noticeService.deleteNotice(user, noticeIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = noticeController.deleteNotice(user, noticeIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(noticeService).deleteNotice(user, noticeIdx);
    }

    private User createUser(Integer id, String name, Role role) {
        return User.builder()
                .id(id)
                .name(name)
                .role(role)
                .build();
    }

    private SearchNoticeResponse createSearchNoticeResponse(Integer id) {
        SearchNoticeUserResponse userResponse = new SearchNoticeUserResponse(1, "작성자");
        List<SearchNoticeAttachmentResponse> attachments = Arrays.asList(
                new SearchNoticeAttachmentResponse(1, "file1.txt", "/path/to/file1"),
                new SearchNoticeAttachmentResponse(2, "file2.txt", "/path/to/file2")
        );

        return new SearchNoticeResponse(
                id,
                "테스트 공지",
                "테스트 내용",
                true,
                attachments,
                LocalDateTime.now(),
                userResponse
        );
    }
}