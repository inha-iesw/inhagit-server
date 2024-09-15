package inha.git.question.api.mapper;

import inha.git.common.BaseEntity.State;
import inha.git.field.domain.Field;
import inha.git.mapping.domain.*;
import inha.git.mapping.domain.id.*;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.project.domain.Project;
import inha.git.question.api.controller.dto.request.*;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionResponse;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.QuestionReplyComment;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.domain.Semester;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * QuestionMapper는 Question 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface QuestionMapper {

    /**
     * CreateQuestionRequest를 Question으로 변환합니다.
     *
     * @param createQuestionRequest CreateQuestionRequest
     * @param user                  User
     * @return Question
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjectName", source = "createQuestionRequest.subject")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "semester", source = "semester")
    @Mapping(target = "likeCount", constant = "0")
    Question createQuestionRequestToQuestion(CreateQuestionRequest createQuestionRequest, User user, Semester semester);

    /**
     * Question을 QuestionResponse로 변환합니다.
     *
     * @param question Question
     * @return QuestionResponse
     */
    @Mapping(target = "idx", source = "question.id")
    QuestionResponse questionToQuestionResponse(Question question);


    /**
     * QuestionField를 생성합니다.
     *
     * @param question Question
     * @param field    Field
     * @return QuestionField
     */
    default QuestionField createQuestionField(Question question, Field field) {
        return new QuestionField(new QuestionFieldId(question.getId(), field.getId()), question, field);
    }

    /**
     * CreateQuestionRequest를 Question으로 업데이트합니다.
     *
     * @param updateQuestionRequest UpdateQuestionRequest
     * @param question              Question
     */
    @Mapping(target = "subjectName", source = "updateQuestionRequest.subject")
    @Mapping(target = "title", source = "updateQuestionRequest.title")
    @Mapping(target = "contents", source = "updateQuestionRequest.contents")
    @Mapping(target = "semester", source = "semester")
    void updateQuestionRequestToQuestion(UpdateQuestionRequest updateQuestionRequest, @MappingTarget Question question, Semester semester);

    /**
     * Field를 SearchFieldResponse로 변환합니다.
     *
     * @param field Field
     * @return SearchFieldResponse
     */
    @Mapping(target = "idx", source = "field.id")
    @Mapping(target = "name", source = "field.name")
    SearchFieldResponse projectFieldToSearchFieldResponse(Field field);

    /**
     * Question을 SearchQuestionResponse로 변환합니다.
     *
     * @param question  Question
     * @param fieldList List<SearchFieldResponse>
     * @param author    SearchUserResponse
     * @param semester SearchSemesterResponse
     * @return SearchQuestionResponse
     */
    @Mapping(target = "idx", source = "question.id")
    @Mapping(target = "subject", source = "question.subjectName")
    @Mapping(target = "createdAt", source = "question.createdAt")
    @Mapping(target = "semester", source = "semester")
    @Mapping(target = "likeCount", source = "question.likeCount")
    SearchQuestionResponse questionToSearchQuestionResponse(Question question, List<SearchFieldResponse> fieldList, SearchUserResponse author, SearchSemesterResponse semester);

    /**
     * User를 SearchUserResponse로 변환합니다.
     *
     * @param user User
     * @return SearchUserResponse
     */
    @Mapping(target = "idx", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    SearchUserResponse userToSearchUserResponse(User user);

    /**
     * CreateCommentRequest를 QuestionComment로 변환합니다.
     *
     * @param createCommentRequest CreateCommentRequest
     * @param user                 User
     * @param question             Question
     * @return QuestionComment
     */
    @Mapping(target = "contents", source = "createCommentRequest.contents")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "question", source = "question")
    QuestionComment toQuestionComment(CreateCommentRequest createCommentRequest, User user, Question question);

    /**
     * QuestionComment를 CommentResponse로 변환합니다.
     *
     * @param questionComment QuestionComment
     * @return CommentResponse
     */
    @Mapping(target = "idx", source = "questionComment.id")
    CommentResponse toCommentResponse(QuestionComment questionComment);

    /**
     * CreateReplyCommentRequest를 QuestionReplyComment로 변환합니다.
     *
     * @param createReplyCommentRequest CreateReplyCommentRequest
     * @param user                     User
     * @param questionComment          QuestionComment
     * @return QuestionReplyComment
     */
    @Mapping(target = "contents", source = "createReplyCommentRequest.contents")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "likeCount", constant = "0")
    @Mapping(target = "questionComment", source = "questionComment")
    QuestionReplyComment toQuestionReplyComment(CreateReplyCommentRequest createReplyCommentRequest, User user, QuestionComment questionComment);

    /**
     * QuestionReplyComment를 ReplyCommentResponse로 변환합니다.
     *
     * @param questionReplyComment QuestionReplyComment
     * @return ReplyCommentResponse
     */
    @Mapping(target = "idx", source = "questionReplyComment.id")
    ReplyCommentResponse toReplyCommentResponse(QuestionReplyComment questionReplyComment);

    /**
     * QuestionComment를 CommentWithRepliesResponse로 변환합니다.
     *
     * @param questionComment QuestionComment
     * @return CommentWithRepliesResponse
     */
    @Mapping(target = "idx", source = "questionComment.id")
    @Mapping(target = "author", source = "questionComment.user")
    @Mapping(target = "createdAt", source = "questionComment.createdAt")
    @Mapping(target = "contents", source = "questionComment.contents")
    @Mapping(target = "likeCount", source = "questionComment.likeCount")
    @Mapping(target = "replies", expression = "java(filterActiveReplies(questionComment.getReplies()))")
    CommentWithRepliesResponse toCommentWithRepliesResponse(QuestionComment questionComment);

    /**
     * QuestionReplyComment 목록을 SearchReplyCommentResponse 목록으로 변환합니다.
     *
     * @param replies List<QuestionReplyComment>
     * @return List<SearchReplyCommentResponse>
     */
    default List<SearchReplyCommentResponse> filterActiveReplies(List<QuestionReplyComment> replies) {
        return replies.stream()
                .filter(reply -> reply.getState() == State.ACTIVE)  // 상태가 ACTIVE인 대댓글만 필터링
                .map(this::toSearchReplyCommentResponse)  // SearchReplyCommentResponse로 매핑
                .toList();
    }
    /**
     * QuestionReplyComment를 SearchReplyCommentResponse로 변환합니다.
     *
     * @param questionReplyComment QuestionReplyComment
     * @return SearchReplyCommentResponse
     */
    @Mapping(target = "idx", source = "questionReplyComment.id")
    @Mapping(target = "author", source = "questionReplyComment.user")
    @Mapping(target = "createdAt", source = "questionReplyComment.createdAt")
    @Mapping(target = "likeCount", source = "questionReplyComment.likeCount")
    @Mapping(target = "contents", source = "questionReplyComment.contents")
    SearchReplyCommentResponse toSearchReplyCommentResponse(QuestionReplyComment questionReplyComment);


    /**
     * QuestionComment 목록을 CommentWithRepliesResponse 목록으로 변환합니다.
     *
     * @param comments List<QuestionComment>
     * @return List<CommentWithRepliesResponse>
     */
    List<CommentWithRepliesResponse> toCommentWithRepliesResponseList(List<QuestionComment> comments);


    default QuestionCommentLike createQuestionCommentLike(User user, QuestionComment questionComment) {
        return new QuestionCommentLike(new QuestionCommentLikeId(user.getId(), questionComment.getId()), questionComment, user);
    }

    /**
     * QuestionReplyCommentLike를 생성합니다.
     *
     * @param user               User
     * @param questionReplyComment QuestionReplyComment
     * @return QuestionReplyCommentLike
     */
    default QuestionReplyCommentLike createQuestionReplyCommentLike(User user, QuestionReplyComment questionReplyComment) {
        return new QuestionReplyCommentLike(new QuestionReplyCommentLikeId(user.getId(), questionReplyComment.getId()), questionReplyComment, user);
    }

    /**
     * QuestionLike를 생성합니다.
     *
     * @param user     User
     * @param question Question
     * @return QuestionLike
     */
    default QuestionLike createQuestionLike(User user, Question question) {
        return new QuestionLike(new QuestionLikeId(user.getId(), question.getId()), question, user);
    }


}
