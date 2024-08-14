package inha.git.notice.api.service;

import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticesResponse;
import inha.git.user.domain.User;
import org.springframework.data.domain.Page;

public interface NoticeService {
    Page<SearchNoticesResponse> getNotices(Integer page);
    String createNotice(User user, CreateNoticeRequest createNoticeRequest);

    String updateNotice(User user, Integer noticeIdx, UpdateNoticeRequest updateNoticeRequest);

    String deleteNotice(User user, Integer noticeIdx);

    SearchNoticeResponse getNotice(Integer noticeIdx);
}
