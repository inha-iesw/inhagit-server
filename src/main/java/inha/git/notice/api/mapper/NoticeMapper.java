package inha.git.notice.api.mapper;

import inha.git.notice.api.controller.dto.request.CreateNoticeRequest;
import inha.git.notice.domain.Notice;
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
    Notice createNoticeRequestToNotice(User user, CreateNoticeRequest createNoticeRequest);
}
