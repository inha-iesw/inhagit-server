package inha.git.admin.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.mapping.domain.QUserDepartment;
import inha.git.user.domain.QProfessor;
import inha.git.user.domain.QUser;
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
public class AdminQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<SearchProfessorResponse> searchProfessors(String search, Pageable pageable) {
        QUser user = QUser.user;
        QProfessor professor = QProfessor.professor;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        var query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(professor).on(user.id.eq(professor.user.id))
                .leftJoin(user.userDepartments, userDepartment)
                .where(
                        user.role.eq(Role.PROFESSOR),
                        nameLike(search)
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SearchProfessorResponse> content = query.fetch().stream()
                .map(u -> new SearchProfessorResponse(
                        u,
                        u.getProfessor()
                ))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression nameLike(String search) {
        return StringUtils.hasText(search) ? QUser.user.name.contains(search) : null;
    }
}
