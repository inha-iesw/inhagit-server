package inha.git.notice.api.service;

import inha.git.common.BaseEntity;
import inha.git.common.exceptions.BaseException;
import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.mapper.NoticeMapper;
import inha.git.notice.domain.Notice;
import inha.git.notice.domain.repository.NoticeJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.code.status.ErrorStatus.NOTICE_NOT_AUTHORIZED;
import static inha.git.common.code.status.ErrorStatus.NOTICE_NOT_FOUND;

/**
 * NoticeServiceImpl는 NoticeService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeJpaRepository noticeJpaRepository;
    private final NoticeMapper noticeMapper;

    /**
     * 공지 생성
     *
     * @param user 사용자
     * @param createNoticeRequest 공지 생성 요청
     * @return 생성된 공지 이름
     */
    @Override
    @Transactional
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
    @Transactional
    public String updateNotice(User user, Integer noticeIdx, UpdateNoticeRequest updateNoticeRequest) {
        Notice notice = noticeJpaRepository.findByIdAndState(noticeIdx, BaseEntity.State.ACTIVE)
                .orElseThrow(() -> new BaseException(NOTICE_NOT_FOUND));

        if(user.getRole() != Role.ADMIN && !notice.getUser().getId().equals(user.getId())) {
            throw new BaseException(NOTICE_NOT_AUTHORIZED);
        }
        notice.updateNotice(updateNoticeRequest.title(), updateNoticeRequest.contents());
        return noticeJpaRepository.save(notice).getTitle() + " 공지가 수정되었습니다.";
    }
}
