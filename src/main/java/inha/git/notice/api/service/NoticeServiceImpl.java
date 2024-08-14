package inha.git.notice.api.service;

import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.mapper.NoticeMapper;
import inha.git.notice.domain.Notice;
import inha.git.notice.domain.repository.NoticeJpaRepository;
import inha.git.notice.domain.repository.NoticeQueryRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.code.status.ErrorStatus.NOTICE_NOT_AUTHORIZED;
import static inha.git.common.code.status.ErrorStatus.NOTICE_NOT_FOUND;

/**
 * NoticeServiceImpl는 NoticeService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeJpaRepository noticeJpaRepository;
    private final NoticeMapper noticeMapper;
    private final NoticeQueryRepository noticeQueryRepository;

    @Override
    public Page<SearchNoticeResponse> getNotices(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return noticeQueryRepository.getNotices(pageable);
    }

    /**
     * 공지 생성
     *
     * @param user 사용자
     * @param createNoticeRequest 공지 생성 요청
     * @return 생성된 공지 이름
     */
    @Override
    public String createNotice(User user, CreateNoticeRequest createNoticeRequest) {
        Notice notice = noticeMapper.createNoticeRequestToNotice(user, createNoticeRequest);
        return noticeJpaRepository.save(notice).getTitle() + " 공지가 생성되었습니다.";
    }

    /**
     * 공지 수정
     *
     * <p>관리자는 모든 공지를 수정할 수 있고, 공지 작성자는 자신의 공지만 수정할 수 있습니다.</p>
     *
     * @param user 사용자
     * @param noticeIdx 공지 인덱스
     * @param updateNoticeRequest 공지 수정 요청
     * @return 수정된 공지 이름
     */
    @Override
    public String updateNotice(User user, Integer noticeIdx, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = getNotice(noticeIdx);
        validateUserAuthorization(user, notice);
        notice.updateNotice(updateNoticeRequest.title(), updateNoticeRequest.contents());
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
        Notice notice = getNotice(noticeIdx);
        validateUserAuthorization(user, notice);
        notice.setState(INACTIVE);
        notice.setDeletedAt();
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
            throw new BaseException(NOTICE_NOT_AUTHORIZED);
        }
    }

    /**
     * 공지 조회
     *
     * @param noticeIdx 공지 인덱스
     * @return 공지
     */
    private Notice getNotice(Integer noticeIdx) {
        return noticeJpaRepository.findByIdAndState(noticeIdx, BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOTICE_NOT_FOUND));
    }
}
