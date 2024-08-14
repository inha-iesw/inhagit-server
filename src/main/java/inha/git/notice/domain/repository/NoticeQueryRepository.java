package inha.git.notice.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.notice.api.controller.dto.response.SearchNoticeResponse;
import inha.git.notice.api.controller.dto.response.SearchNoticeUserResponse;
import inha.git.notice.domain.Notice;
import inha.git.notice.domain.QNotice;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;
    public Page<SearchNoticeResponse> getNotices(Pageable pageable) {
        QNotice notice = QNotice.notice;
        QUser user = QUser.user;

        JPAQuery<Notice> query = queryFactory
                .select(notice)
                .from(notice)
                .leftJoin(notice.user, user)
                .orderBy(notice.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Notice> notices = query.fetch();
        long total = query.fetchCount();
        List<SearchNoticeResponse> content = notices.stream()
                .map(n -> new SearchNoticeResponse(
                        n.getId(),
                        n.getTitle(),
                        n.getCreatedAt(),
                        new SearchNoticeUserResponse(
                                n.getUser().getId(),
                                n.getUser().getName()
                        )
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
