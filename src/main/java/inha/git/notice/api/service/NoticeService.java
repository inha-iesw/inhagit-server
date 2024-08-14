package inha.git.notice.api.service;

import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.request.UpdateNoticeRequest;
import inha.git.user.domain.User;

public interface NoticeService {
    String createNotice(User user, CreateNoticeRequest createNoticeRequest);

    String updateNotice(User user, Integer noticeIdx, UpdateNoticeRequest updateNoticeRequest);
}
