package inha.git.notice.api.mapper;

import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticeUserResponse;
import inha.git.notice.domain.Notice;
import inha.git.notice.domain.NoticeAttachment;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * NoticeMapper는 Notice 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NoticeMapper {

    /**
     * CreateNoticeRequest를 Notice 엔티티로 변환
     *
     * @param user 사용자
     * @param createNoticeRequest 공지 생성 요청
     * @return Notice 엔티티
     */
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", ignore = true)
    Notice createNoticeRequestToNotice(User user, CreateNoticeRequest createNoticeRequest);

    /**
     * Notice를 SearchNoticeResponse로 변환
     *
     * @param notice 공지
     * @param author 작성자
     * @return SearchNoticeResponse
     */
    @Mapping(target = "idx", source = "notice.id")
    SearchNoticeResponse noticeToSearchNoticeResponse(Notice notice, SearchNoticeUserResponse author);

    /**
     * User를 SearchNoticeUserResponse로 변환
     *
     * @param user 사용자
     * @return SearchNoticeUserResponse
     */
    @Mapping(target = "idx", source = "id")
    @Mapping(target = "name", source = "name")
    SearchNoticeUserResponse userToSearchNoticeUserResponse(User user);


    /**
     * CreateNoticeRequest를 Notice 엔티티로 변환
     *
     * @param originalFileName 원본 파일 이름
     * @param storedFileUrl 저장된 파일 URL
     * @return NoticeAttachment 엔티티
     */
    @Mapping(target = "id", ignore = true)
    NoticeAttachment createNoticeAttachmentRequestToNoticeAttachment(String originalFileName, String storedFileUrl, Notice notice);
}
