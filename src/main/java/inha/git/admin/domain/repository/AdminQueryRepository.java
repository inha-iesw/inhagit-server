package inha.git.admin.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.admin.api.controller.dto.response.SearchCompanyResponse;
import inha.git.admin.api.controller.dto.response.SearchProfessorResponse;
import inha.git.admin.api.controller.dto.response.SearchStudentResponse;
import inha.git.admin.api.controller.dto.response.SearchUserResponse;
import inha.git.common.BaseEntity.State;
import inha.git.mapping.domain.QUserDepartment;
import inha.git.user.domain.QCompany;
import inha.git.user.domain.QProfessor;
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
public class AdminQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 사용자 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 사용자 목록
     */
    public Page<SearchUserResponse> searchUsers(String search, Pageable pageable) {
        QUser user = QUser.user;

        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .where(nameLike(search),user.state.eq(State.ACTIVE))
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());
        List<SearchUserResponse> content = query.fetch().stream()
                .map(u -> new SearchUserResponse(u))
                .toList();
        long total = query.fetchCount();
        return new PageImpl<>(content, pageable, total);
    }

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
                        user.role.eq(Role.USER),
                        nameLike(search),
                        user.state.eq(State.ACTIVE)
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
     * 교수 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 교수 목록
     */
    public Page<SearchProfessorResponse> searchProfessors(String search, Pageable pageable) {
        QUser user = QUser.user;
        QProfessor professor = QProfessor.professor;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(professor).on(user.id.eq(professor.user.id))
                .leftJoin(user.userDepartments, userDepartment)
                .where(
                        user.role.eq(Role.PROFESSOR),
                        nameLike(search),
                        user.state.eq(State.ACTIVE)
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
    /**
     * 회사 검색
     *
     * @param search   검색어
     * @param pageable 페이지 정보
     * @return 회사 목록
     */
    public Page<SearchCompanyResponse> searchCompanies(String search, Pageable pageable) {
        QUser user = QUser.user;
        QCompany company = QCompany.company;
        QUserDepartment userDepartment = QUserDepartment.userDepartment;
        JPAQuery<User> query = queryFactory
                .select(user)
                .from(user)
                .leftJoin(user.userDepartments, userDepartment)
                .leftJoin(company).on(user.id.eq(company.user.id))
                .where(
                        user.role.eq(Role.COMPANY),
                        nameLike(search),
                        user.state.eq(State.ACTIVE)
                )
                .orderBy(user.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<SearchCompanyResponse> content = query.fetch().stream()
                .map(u -> new SearchCompanyResponse(u, u.getCompany()))
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
