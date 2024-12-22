package inha.git.notice.api.service;

import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeAttachmentResponse;
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
 * 공지사항 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 공지사항의 조회, 생성, 수정, 삭제 및 첨부파일 관리 기능을 제공합니다.
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
     * 공지사항 목록을 페이징하여 조회합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 페이지 정보로 Pageable 객체 생성 (작성일 기준 내림차순 정렬)<br>
     * 2. QueryDSL을 사용하여 페이징된 공지사항 목록 조회<br>
     * </p>
     *
     * @param page 조회할 페이지 번호 (0부터 시작)
     * @param size 페이지당 항목 수
     * @return 페이징된 공지사항 목록
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SearchNoticesResponse> getNotices(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return noticeQueryRepository.getNotices(pageable);
    }

    /**
     * 특정 공지사항의 상세 정보를 조회합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 공지사항 ID로 공지사항 조회<br>
     * 2. 작성자 정보 조회<br>
     * 3. 첨부파일 정보 매핑<br>
     * 4. 응답 DTO 생성 및 반환<br>
     * </p>
     *
     * @param noticeIdx 조회할 공지사항 ID
     * @return 공지사항 상세 정보
     * @throws BaseException NOT_FIND_USER: 작성자를 찾을 수 없는 경우
     *                      NOTICE_NOT_FOUND: 공지사항을 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public SearchNoticeResponse getNotice(Integer noticeIdx) {
        Notice notice = findNotice(noticeIdx);
        User user = userJpaRepository.findById(notice.getUser().getId())
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        List<SearchNoticeAttachmentResponse> noticeAttachments = notice.getNoticeAttachments().stream()
                .map(noticeMapper::noticeAttachmentToSearchNoticeAttachmentResponse)
                .toList();
        return noticeMapper.noticeToSearchNoticeResponse(notice, noticeMapper.userToSearchNoticeUserResponse(user), noticeAttachments);
    }

    /**
     * 새로운 공지사항을 생성합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 중복 요청 검증<br>
     * 2. 공지사항 엔티티 생성 및 저장<br>
     * 3. 첨부파일이 있는 경우 파일 저장 및 엔티티 생성<br>
     * 4. 트랜잭션 롤백 시 파일 삭제 등록<br>
     * </p>
     *
     * @param user 생성을 요청한 사용자 정보
     * @param createNoticeRequest 생성할 공지사항 정보
     * @param attachmentList 첨부파일 목록 (선택적)
     * @return 공지사항 생성 완료 메시지
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
     * 기존 공지사항을 수정합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 공지사항 조회 및 권한 검증<br>
     * 2. 제목, 내용 수정<br>
     * 3. 기존 첨부파일 삭제 (파일 시스템 및 DB)<br>
     * 4. 새로운 첨부파일 저장 (있는 경우)<br>
     * 5. 트랜잭션 롤백 시 파일 삭제 등록<br>
     * </p>
     *
     * @param user 수정을 요청한 사용자 정보
     * @param noticeIdx 수정할 공지사항 ID
     * @param updateNoticeRequest 수정할 내용
     * @param attachmentList 새로운 첨부파일 목록 (선택적)
     * @return 공지사항 수정 완료 메시지
     * @throws BaseException NOTICE_NOT_FOUND: 공지사항을 찾을 수 없는 경우
     *                      NOTICE_NOT_AUTHORIZED: 수정 권한이 없는 경우
     */
    @Override
    public String updateNotice(User user, Integer noticeIdx, UpdateNoticeRequest updateNoticeRequest, List<MultipartFile> attachmentList) {
        Notice notice = findNotice(noticeIdx);
        validateUserAuthorization(user, notice);
        notice.updateNotice(updateNoticeRequest.title(), updateNoticeRequest.contents());

        // 기존 첨부파일들의 실제 파일 삭제 및 DB에서 삭제
        if (notice.getNoticeAttachments() != null && !notice.getNoticeAttachments().isEmpty()) {
            notice.setHasAttachment(false);
            notice.getNoticeAttachments().forEach(attachment -> {
                // 실제 파일 삭제
                FilePath.deleteFile(BASE_DIR_SOURCE_2 + attachment.getStoredFileUrl());
                // DB에서 삭제
                noticeAttachmentRepository.delete(attachment);
            });
            notice.setNoticeAttachments(new ArrayList<>());
        }

        if (attachmentList != null && !attachmentList.isEmpty()) {
            notice.setHasAttachment(true);
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
     * 공지사항을 삭제(비활성화) 처리합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 공지사항 조회 및 권한 검증<br>
     * 2. 상태를 INACTIVE로 변경<br>
     * 3. 삭제 시간 기록<br>
     * </p>
     *
     * @param user 삭제를 요청한 사용자 정보
     * @param noticeIdx 삭제할 공지사항 ID
     * @return 공지사항 삭제 완료 메시지
     * @throws BaseException NOTICE_NOT_FOUND: 공지사항을 찾을 수 없는 경우
     *                      NOTICE_NOT_AUTHORIZED: 삭제 권한이 없는 경우
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

    /**
     * 트랜잭션 롤백 시 파일 삭제 로직 등록
     *
     * @param zipFilePath 파일 경로
     */
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
