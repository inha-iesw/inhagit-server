package inha.git.question.domain.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.question.api.controller.dto.request.SearchQuestionCond;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.domain.Question;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inha.git.common.Constant.mapRoleToPosition;
import static inha.git.mapping.domain.QQuestionField.questionField;
import static inha.git.question.domain.QQuestion.question;
import static inha.git.user.domain.QUser.user;

/**
 * 질문 조회 관련 레포지토리
 */
@Repository
@RequiredArgsConstructor
public class QuestionQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 질문 목록 조회
     *
     * @param pageable 페이지 정보
     * @return 질문 페이지
     */
    public Page<SearchQuestionsResponse> getQuestions(Pageable pageable) {

        JPAQuery<Question> query = queryFactory
                .select(question)
                .from(question)
                .leftJoin(question.user, user)
                .leftJoin(question.questionFields, questionField)
                .where(question.state.eq(Question.State.ACTIVE))
                .orderBy(question.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Question> questions = query.fetch();
        long total = query.fetchCount();

        List<SearchQuestionsResponse> content = questions.stream()
                .map(q -> new SearchQuestionsResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getCreatedAt(),
                        q.getSubjectName(),
                        new SearchSemesterResponse(
                                q.getSemester().getId(),
                                q.getSemester().getName()
                        ),
                        q.getLikeCount(),
                        q.getCommentCount(),
                        q.getQuestionFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                q.getUser().getId(),
                                q.getUser().getName(),
                                mapRoleToPosition(q.getUser().getRole())
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 사용자의 질문 목록 조회
     *
     * @param id       사용자 번호
     * @param pageable 페이지 정보
     * @return 질문 페이지
     */
    public Page<SearchQuestionsResponse> getUserQuestions(Integer id, Pageable pageable) {

        JPAQuery<Question> query = queryFactory
                .select(question)
                .from(question)
                .leftJoin(question.user, user)
                .leftJoin(question.questionFields, questionField)
                .where(question.state.eq(Question.State.ACTIVE).and(question.user.id.eq(id)))
                .orderBy(question.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Question> questions = query.fetch();
        long total = query.fetchCount();

        List<SearchQuestionsResponse> content = questions.stream()
                .map(q -> new SearchQuestionsResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getCreatedAt(),
                        q.getSubjectName(),
                        new SearchSemesterResponse(
                                q.getSemester().getId(),
                                q.getSemester().getName()
                        ),
                        q.getLikeCount(),
                        q.getCommentCount(),
                        q.getQuestionFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                q.getUser().getId(),
                                q.getUser().getName(),
                                mapRoleToPosition(q.getUser().getRole())
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 질문 조건 조회
     *
     * @param searchQuestionCond 질문 검색 조건
     * @param pageable           페이지 정보
     * @return 질문 페이지
     */
    public Page<SearchQuestionsResponse> getCondQuestions(SearchQuestionCond searchQuestionCond, Pageable pageable) {
        // 동적 조건 생성
        BooleanExpression condition = question.state.eq(Question.State.ACTIVE);

        // 단과대 조건 추가
        if (searchQuestionCond.collegeIdx() != null) {
            condition = condition.and(user.userDepartments.any().department.college.id.eq(searchQuestionCond.collegeIdx()));
        }

        // 학과 조건 추가
        if (searchQuestionCond.departmentIdx() != null) {
            condition = condition.and(user.userDepartments.any().department.id.eq(searchQuestionCond.departmentIdx()));
        }

        // 학기 조건 추가
        if (searchQuestionCond.semesterIdx() != null) {
            condition = condition.and(question.semester.id.eq(searchQuestionCond.semesterIdx()));
        }

        // 분야 조건 추가
        if (searchQuestionCond.fieldIdx() != null) {
            condition = condition.and(questionField.field.id.eq(searchQuestionCond.fieldIdx()));
        }

        // 과목명 조건: LIKE '%subject%'
        if (searchQuestionCond.subject() != null && !searchQuestionCond.subject().isEmpty()) {
            condition = condition.and(question.subjectName.containsIgnoreCase(searchQuestionCond.subject()));
        }

        if (searchQuestionCond.title() != null && !searchQuestionCond.title().isEmpty()) {
            condition = condition.and(question.title.containsIgnoreCase(searchQuestionCond.title()));
        }
        // 질문 목록 조회 쿼리
        JPAQuery<Question> query = queryFactory
                .select(question)
                .from(question)
                .leftJoin(question.user, user)
                .leftJoin(question.questionFields, questionField)
                .where(condition)
                .orderBy(question.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 결과 리스트 및 총 개수 가져오기
        List<Question> questions = query.fetch();
        long total = query.fetchCount();

        // SearchQuestionsResponse 변환
        List<SearchQuestionsResponse> content = questions.stream()
                .map(q -> new SearchQuestionsResponse(
                        q.getId(),
                        q.getTitle(),
                        q.getCreatedAt(),
                        q.getSubjectName(),
                        new SearchSemesterResponse(
                                q.getSemester().getId(),
                                q.getSemester().getName()
                        ),
                        q.getLikeCount(),
                        q.getCommentCount(),
                        q.getQuestionFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                q.getUser().getId(),
                                q.getUser().getName(),
                                mapRoleToPosition(q.getUser().getRole())
                        )
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}
