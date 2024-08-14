package inha.git.notice.api.service;

import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.mapper.NoticeMapper;
import inha.git.notice.domain.Notice;
import inha.git.notice.domain.repository.NoticeJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
