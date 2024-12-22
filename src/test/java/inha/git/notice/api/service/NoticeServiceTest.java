package inha.git.notice.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeAttachmentResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticeUserResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticesResponse;
import inha.git.notice.api.mapper.NoticeMapper;
import inha.git.notice.domain.Notice;
import inha.git.notice.domain.NoticeAttachment;
import inha.git.notice.domain.repository.NoticeAttachmentJpaRepository;
import inha.git.notice.domain.repository.NoticeJpaRepository;
import inha.git.notice.domain.repository.NoticeQueryRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.IdempotentProvider;
import inha.git.utils.file.FilePath;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.code.status.ErrorStatus.NOTICE_NOT_AUTHORIZED;
import static inha.git.common.code.status.ErrorStatus.NOTICE_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @InjectMocks
    private NoticeServiceImpl noticeService;

    @Mock
    private NoticeJpaRepository noticeJpaRepository;

    @Mock
    private NoticeAttachmentJpaRepository noticeAttachmentRepository;

    @Mock
    private NoticeMapper noticeMapper;

    @Mock
    private NoticeQueryRepository noticeQueryRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private IdempotentProvider idempotentProvider;

    @Mock
    private FilePath filePath;  // FilePath Mock 추가

    @Test
    @DisplayName("공지사항 페이징 조회 성공")
    void getNotices_Success() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        Page<SearchNoticesResponse> expectedPage = new PageImpl<>(Arrays.asList(
                new SearchNoticesResponse(1, "공지1",  LocalDateTime.now(), false, new SearchNoticeUserResponse(1, "작성자1")),
                new SearchNoticesResponse(2, "공지2",  LocalDateTime.now(), false, new SearchNoticeUserResponse(2, "작성자2"))
        ));

        given(noticeQueryRepository.getNotices(pageable))
                .willReturn(expectedPage);

        // when
        Page<SearchNoticesResponse> result = noticeService.getNotices(page, size);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(noticeQueryRepository).getNotices(pageable);
    }

    //@Test
    @DisplayName("공지사항 상세 조회 성공")
    void getNotice_Success() {
        // given
        Integer noticeIdx = 1;
        Notice notice = createNotice(noticeIdx, "테스트 공지");
        User user = createUser(1, "작성자", Role.ASSISTANT);
        notice.setUser(user);

        ArrayList<NoticeAttachment> attachments = new ArrayList<>();
        attachments.add(createNoticeAttachment(1, "file1.txt", "/path/to/file1", notice));
        attachments.add(createNoticeAttachment(2, "file2.txt", "/path/to/file2", notice));
        notice.setNoticeAttachments(attachments);

        SearchNoticeUserResponse userResponse = new SearchNoticeUserResponse(user.getId(), user.getName());
        List<SearchNoticeAttachmentResponse> attachmentResponses = Arrays.asList(
                new SearchNoticeAttachmentResponse(1, "file1.txt", "/path/to/file1"),
                new SearchNoticeAttachmentResponse(2, "file2.txt", "/path/to/file2")
        );

        SearchNoticeResponse expectedResponse = new SearchNoticeResponse(
                notice.getId(),
                notice.getTitle(),
                notice.getContents(),
                true,
                attachmentResponses,
                notice.getCreatedAt(),
                userResponse
        );

        given(noticeJpaRepository.findByIdAndState(noticeIdx, ACTIVE))
                .willReturn(Optional.of(notice));
        given(userJpaRepository.findById(user.getId()))
                .willReturn(Optional.of(user));
        given(noticeMapper.noticeToSearchNoticeResponse(eq(notice), any(), anyList()))
                .willReturn(expectedResponse);

        // when
        SearchNoticeResponse result = noticeService.getNotice(noticeIdx);

        // then
        assertThat(result).isEqualTo(expectedResponse);
    }


    @Test
    @DisplayName("존재하지 않는 공지사항 조회 시 예외 발생")
    void getNotice_NotFound_ThrowsException() {
        // given
        Integer noticeIdx = 999;
        given(noticeJpaRepository.findByIdAndState(noticeIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> noticeService.getNotice(noticeIdx));
        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(NOTICE_NOT_FOUND.getMessage());
    }


    //@Test
    @DisplayName("첨부파일이 있는 공지사항 생성 성공")
    void createNotice_WithAttachments_Success() {
        // given
        User user = createUser(1, "작성자", Role.ASSISTANT);
        CreateNoticeRequest request = new CreateNoticeRequest("제목", "내용");
        Notice notice = createNotice(1, "제목");
        notice.setUser(user);

        List<MultipartFile> attachments = Arrays.asList(
                new MockMultipartFile("file1", "file1.txt", "text/plain", "test content".getBytes()),
                new MockMultipartFile("file2", "file2.txt", "text/plain", "test content".getBytes())
        );

        given(noticeMapper.createNoticeRequestToNotice(user, request))
                .willReturn(notice);
        given(noticeJpaRepository.save(any(Notice.class)))
                .willReturn(notice);
        doNothing().when(idempotentProvider)
                .isValidIdempotent(anyList());

        // Mock 파일 저장 로직
        try (MockedStatic<FilePath> filePathMock = mockStatic(FilePath.class)) {
            filePathMock.when(() -> FilePath.storeFile(any(MultipartFile.class), any()))
                    .thenReturn("/mocked/path/file.txt");

            // when
            String result = noticeService.createNotice(user, request, attachments);

            // then
            assertThat(result).isEqualTo("제목 공지가 생성되었습니다.");
            assertThat(notice.getHasAttachment()).isTrue();
            verify(noticeJpaRepository).save(notice);
            verify(noticeAttachmentRepository, times(2)).save(any(NoticeAttachment.class));
        }
    }

    @Test
    @DisplayName("권한이 없는 사용자의 공지사항 수정 시도 시 예외 발생")
    void updateNotice_WithoutAuthorization_ThrowsException() {
        // given
        User unauthorized = createUser(2, "무권한사용자", Role.USER);
        Integer noticeIdx = 1;
        Notice notice = createNotice(1, "제목");
        User originalAuthor = createUser(1, "원작성자", Role.ASSISTANT);
        notice.setUser(originalAuthor);
        UpdateNoticeRequest request = new UpdateNoticeRequest("수정된제목", "수정된내용");

        given(noticeJpaRepository.findByIdAndState(noticeIdx, ACTIVE))
                .willReturn(Optional.of(notice));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> noticeService.updateNotice(unauthorized, noticeIdx, request, null));
        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(NOTICE_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("관리자의 타인 공지사항 수정 성공")
    void updateNotice_ByAdmin_Success() {
        // given
        User admin = createUser(2, "관리자", Role.ADMIN);
        Integer noticeIdx = 1;
        Notice notice = createNotice(1, "제목");
        notice.setUser(createUser(1, "원작성자", Role.ASSISTANT));
        UpdateNoticeRequest request = new UpdateNoticeRequest("수정된제목", "수정된내용");

        given(noticeJpaRepository.findByIdAndState(noticeIdx, ACTIVE))
                .willReturn(Optional.of(notice));
        given(noticeJpaRepository.save(any(Notice.class)))
                .willReturn(notice);

        // when
        String result = noticeService.updateNotice(admin, noticeIdx, request, null);

        // then
        assertThat(result).isEqualTo("수정된제목 공지가 수정되었습니다.");
        assertThat(notice.getTitle()).isEqualTo("수정된제목");
        assertThat(notice.getContents()).isEqualTo("수정된내용");
    }

    @Test
    @DisplayName("공지사항 삭제 성공")
    void deleteNotice_Success() {
        // given
        User user = createUser(1, "작성자", Role.ASSISTANT);
        Integer noticeIdx = 1;
        Notice notice = createNotice(noticeIdx, "삭제될공지");
        notice.setUser(user);

        given(noticeJpaRepository.findByIdAndState(noticeIdx, ACTIVE))
                .willReturn(Optional.of(notice));
        given(noticeJpaRepository.save(any(Notice.class)))
                .willReturn(notice);

        // when
        String result = noticeService.deleteNotice(user, noticeIdx);

        // then
        assertThat(result).isEqualTo("삭제될공지 공지가 삭제되었습니다.");
        assertThat(notice.getState()).isEqualTo(INACTIVE);
        assertThat(notice.getDeletedAt()).isNotNull();
    }

    private Notice createNotice(Integer id, String title) {
        Notice notice = Notice.builder()
                .id(id)
                .title(title)
                .contents("테스트 내용")
                .hasAttachment(false)
                .build();
        notice.setNoticeAttachments(new ArrayList<>());  // 빈 ArrayList로 초기화
        return notice;
    }

    private NoticeAttachment createNoticeAttachment(Integer id, String originalFileName,
                                                    String storedFileUrl, Notice notice) {
        return NoticeAttachment.builder()
                .id(id)
                .originalFileName(originalFileName)
                .storedFileUrl(storedFileUrl)
                .notice(notice)
                .build();
    }

    private User createUser(Integer id, String name, Role role) {
        return User.builder()
                .id(id)
                .name(name)
                .role(role)
                .build();
    }

    private MultipartFile createMockMultipartFile(String filename) {
        return new MockMultipartFile(
                "file",
                filename,
                MediaType.TEXT_PLAIN_VALUE,
                "test file content".getBytes()
        );
    }
}