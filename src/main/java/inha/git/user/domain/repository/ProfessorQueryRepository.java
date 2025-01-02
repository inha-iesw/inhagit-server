package inha.git.user.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.common.BaseEntity;
import inha.git.mapping.domain.QUserDepartment;
import inha.git.user.domain.QUser;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProfessorQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 학생 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 학생 목록
     */
    public Page<SearchStudentResponse> searchStudents(String search, Pageable pageable) {
        QUser user = QUser.user;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(user.userDepartments, userDepartment)
                .where(
                        user.role.eq(Role.USER).or(user.role.eq(Role.ASSISTANT)),
                        nameLike(search),
                        user.state.eq(BaseEntity.State.ACTIVE)
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SearchStudentResponse> content = query.fetch().stream()
                .map(u -> new SearchStudentResponse(u))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 이름 검색
     *
     * @param search 검색어
     * @return 검색 조건
     */
    private BooleanExpression nameLike(String search) {
        return StringUtils.hasText(search) ? QUser.user.name.contains(search) : null;
    }
}
