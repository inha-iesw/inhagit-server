package inha.git.question.domain.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inha.git.mapping.domain.QQuestionField;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.question.api.controller.dto.request.SearchQuestionsResponse;
import inha.git.question.domain.QQuestion;
import inha.git.question.domain.Question;
import inha.git.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

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
        QQuestion question = QQuestion.question;
        QUser user = QUser.user;
        QQuestionField questionField = QQuestionField.questionField;

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
                        q.getQuestionFields().stream()
                                .map(f -> new SearchFieldResponse(
                                        f.getField().getId(),
                                        f.getField().getName()
                                ))
                                .toList(),
                        new SearchUserResponse(
                                q.getUser().getId(),
                                q.getUser().getName()
                        )
                ))
                .toList();
        return new PageImpl<>(content, pageable, total);
    }
}
