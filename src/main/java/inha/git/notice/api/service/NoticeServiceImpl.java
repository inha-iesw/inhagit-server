package inha.git.notice.api.service;

import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.*;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * NoticeServiceImpl는 NoticeService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeJpaRepository noticeJpaRepository;
    private final NoticeAttachmentJpaRepository noticeAttachmentRepository;
    private final NoticeMapper noticeMapper;
    private final NoticeQueryRepository noticeQueryRepository;
    private final UserJpaRepository userJpaRepository;
    private final IdempotentProvider idempotentProvider;


    /**
     * 공지 조회
     *
     * @param page 페이지 번호
     * @return 공지 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SearchNoticesResponse> getNotices(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return noticeQueryRepository.getNotices(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public SearchNoticeResponse getNotice(Integer noticeIdx) {
        Notice notice = findNotice(noticeIdx);
        User user = userJpaRepository.findById(notice.getUser().getId())
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        return noticeMapper.noticeToSearchNoticeResponse(notice, noticeMapper.userToSearchNoticeUserResponse(user));
    }

    /**
     * 공지 생성
     *
     * @param user 사용자
     * @param createNoticeRequest 공지 생성 요청
     * @param attachmentList 첨부 파일 리스트
     * @return 생성된 공지 이름
     */
    @Override
    public String createNotice(User user, CreateNoticeRequest createNoticeRequest, List<MultipartFile> attachmentList) {
        idempotentProvider.isValidIdempotent(List.of("createNoticeRequest", user.getId().toString(), user.getName(), createNoticeRequest.title(), createNoticeRequest.contents()));

        Notice notice = noticeMapper.createNoticeRequestToNotice(user, createNoticeRequest);
        Notice savedNotice = noticeJpaRepository.save(notice);

        if (attachmentList != null && !attachmentList.isEmpty()) {
            savedNotice.setHasAttachment(true);
            savedNotice.getNoticeAttachments().addAll(
                    attachmentList.stream()
                            .map(file -> {
                                String originalFileName = file.getOriginalFilename();
                                String storedFileUrl = FilePath.storeFile(file, ATTACHMENT);
                                registerRollbackCleanup(storedFileUrl);
                                NoticeAttachment attachment = noticeMapper.createNoticeAttachmentRequestToNoticeAttachment(originalFileName, storedFileUrl, savedNotice
                                );
                                return noticeAttachmentRepository.save(attachment);
                            })
                            .toList()
            );
        }
        log.info("공지 생성 성공 - 사용자: {} 공지 제목: {}", user.getName(), notice.getTitle());
        return savedNotice.getTitle() + " 공지가 생성되었습니다.";
    }

    /**
     * 공지 수정
     *
     * <p>관리자는 모든 공지를 수정할 수 있고, 공지 작성자는 자신의 공지만 수정할 수 있습니다.</p>
     *
     * @param user 사용자
     * @param noticeIdx 공지 인덱스
     * @param updateNoticeRequest 공지 수정 요청
     * @param attachmentList 첨부 파일 리스트
     * @return 수정된 공지 이름
     */
    @Override
    public String updateNotice(User user, Integer noticeIdx, UpdateNoticeRequest updateNoticeRequest, List<MultipartFile> attachmentList) {
        Notice notice = findNotice(noticeIdx);
        validateUserAuthorization(user, notice);
        notice.updateNotice(updateNoticeRequest.title(), updateNoticeRequest.contents());

        // 기존 첨부파일들의 실제 파일 삭제 및 DB에서 삭제
        if (attachmentList != null && !attachmentList.isEmpty()) {
            notice.setHasAttachment(true);
            notice.getNoticeAttachments().forEach(attachment -> {
                // 실제 파일 삭제
                FilePath.deleteFile(BASE_DIR_SOURCE_2 + attachment.getStoredFileUrl());
                // DB에서 삭제
                noticeAttachmentRepository.delete(attachment);
            });
            notice.setNoticeAttachments(new ArrayList<>());
            notice.getNoticeAttachments().addAll(
                    attachmentList.stream()
                            .map(file -> {
                                String originalFileName = file.getOriginalFilename();
                                String storedFileUrl = FilePath.storeFile(file, ATTACHMENT);
                                // 트랜잭션 롤백 시 파일 삭제를 위한 등록
                                registerRollbackCleanup(storedFileUrl);
                                NoticeAttachment attachment = noticeMapper.createNoticeAttachmentRequestToNoticeAttachment(
                                        originalFileName,
                                        storedFileUrl,
                                        notice
                                );
                                return noticeAttachmentRepository.save(attachment);
                            })
                            .toList()
            );
        }

        log.info("공지 수정 성공 - 사용자: {} 공지 제목: {}", user.getName(), notice.getTitle());
        return noticeJpaRepository.save(notice).getTitle() + " 공지가 수정되었습니다.";
    }

    /**
     * 공지 삭제
     *
     * <p>관리자는 모든 공지를 삭제할 수 있고, 공지 작성자는 자신의 공지만 삭제할 수 있습니다.</p>
     *
     * @param user 사용자
     * @param noticeIdx 공지 인덱스
     * @return 삭제된 공지 이름
     */
    @Override
    public String deleteNotice(User user, Integer noticeIdx) {
        Notice notice = findNotice(noticeIdx);
        validateUserAuthorization(user, notice);
        notice.setState(INACTIVE);
        notice.setDeletedAt();
        log.info("공지 삭제 성공 - 사용자: {} 공지 제목: {}", user.getName(), notice.getTitle());
        return noticeJpaRepository.save(notice).getTitle() + " 공지가 삭제되었습니다.";
    }



    /**
     * 사용자 권한 검증
     *
     * @param user 사용자
     * @param notice 공지
     */
    private static void validateUserAuthorization(User user, Notice notice) {
        if(user.getRole() != Role.ADMIN && !notice.getUser().getId().equals(user.getId())) {
            log.error("공지 수정 실패 {} {} - 권한이 없습니다.", user.getName(), notice.getTitle());
            throw new BaseException(NOTICE_NOT_AUTHORIZED);
        }
    }

    /**
     * 공지 조회
     *
     * @param noticeIdx 공지 인덱스
     * @return 공지
     */
    private Notice findNotice(Integer noticeIdx) {
        return noticeJpaRepository.findByIdAndState(noticeIdx, BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOTICE_NOT_FOUND));
    }

    private void registerRollbackCleanup(String zipFilePath) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    log.info("트랜잭션 롤백 시 파일 삭제 로직 실행");
                    log.info(BASE_DIR_SOURCE_2 + zipFilePath);
                    boolean isFileDeleted = FilePath.deleteFile(BASE_DIR_SOURCE_2 + zipFilePath);

                    if (isFileDeleted ) {
                        log.info("파일이 성공적으로 삭제되었습니다.");
                    } else {
                        log.error("파일 또는 디렉토리 삭제에 실패했습니다.");
                    }
                }
            }
        });
    }
}
