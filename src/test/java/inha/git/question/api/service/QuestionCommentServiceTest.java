package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.QuestionCommentLikeJpaRepository;
import inha.git.mapping.domain.repository.QuestionReplyCommentLikeJpaRepository;
import inha.git.question.api.controller.dto.request.*;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.QuestionReplyComment;
import inha.git.question.domain.repository.QuestionCommentJpaRepository;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.question.domain.repository.QuestionReplyCommentJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.IdempotentProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("질문 댓글 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class QuestionCommentServiceTest {

    @InjectMocks
    private QuestionCommentServiceImpl questionCommentService;

    @Mock
    private QuestionJpaRepository questionJpaRepository;
    @Mock
    private QuestionCommentJpaRepository questionCommentJpaRepository;
    @Mock
    private QuestionCommentLikeJpaRepository questionCommentLikeJpaRepository;
    @Mock
    private QuestionReplyCommentLikeJpaRepository questionReplyCommentLikeJpaRepository;

    @Mock
    private QuestionReplyCommentJpaRepository questionReplyCommentJpaRepository;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private IdempotentProvider idempotentProvider;

    @Test
    @DisplayName("특정 질문의 댓글+대댓글 조회 성공")
    void getAllCommentsByQuestionIdx_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question question = createTestQuestion(100, "질문 제목", "질문 내용", createTestUser(999, "작성자", Role.USER));

        QuestionComment comment = createTestComment(10, createTestUser(2, "댓글작성자", Role.USER), question);
        QuestionReplyComment reply = createTestReply(1001, createTestUser(3, "대댓글작성자", Role.USER), comment);

        comment.setLikeCount(2);
        comment.getReplies().add(reply);

        // 리포지토리 mock
        given(questionJpaRepository.findByIdAndState(100, ACTIVE))
                .willReturn(Optional.of(question));
        given(questionCommentJpaRepository.findAllByQuestionAndStateOrderByIdAsc(question, ACTIVE))
                .willReturn(List.of(comment));

        given(questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, comment))
                .willReturn(true);
        given(questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, reply))
                .willReturn(false);

        SearchReplyCommentResponse fakeReplyRes = new SearchReplyCommentResponse(
                1001,
                "대댓글 내용",
                null,
                1,
                false,
                LocalDateTime.now()
        );
        CommentWithRepliesResponse fakeCommentRes = new CommentWithRepliesResponse(
                10,
                "댓글 내용",
                null,
                LocalDateTime.now(),
                2,
                true,
                List.of(fakeReplyRes)
        );

        given(questionMapper.toSearchReplyCommentResponse(reply, false))
                .willReturn(fakeReplyRes);
        given(questionMapper.toCommentWithRepliesResponse(eq(comment), eq(true), anyList()))
                .willReturn(fakeCommentRes);

        // when
        List<CommentWithRepliesResponse> result =
                questionCommentService.getAllCommentsByQuestionIdx(user, 100);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).idx()).isEqualTo(10);
        assertThat(result.get(0).likeState()).isTrue();
        assertThat(result.get(0).replies()).hasSize(1);
        assertThat(result.get(0).replies().get(0).idx()).isEqualTo(1001);
        assertThat(result.get(0).replies().get(0).likeState()).isFalse();

        verify(questionJpaRepository).findByIdAndState(100, ACTIVE);
        verify(questionCommentJpaRepository).findAllByQuestionAndStateOrderByIdAsc(question, ACTIVE);
        verify(questionMapper).toSearchReplyCommentResponse(reply, false);
        verify(questionMapper).toCommentWithRepliesResponse(eq(comment), eq(true), anyList());
    }

    @Test
    @DisplayName("존재하지 않는 질문 조회 시 예외 발생")
    void getAllCommentsByQuestionIdx_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer invalidQuestionIdx = 999;

        given(questionJpaRepository.findByIdAndState(invalidQuestionIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionCommentService.getAllCommentsByQuestionIdx(user, invalidQuestionIdx));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionCommentJpaRepository, never())
                .findAllByQuestionAndStateOrderByIdAsc(any(), any());
    }

    @Test
    @DisplayName("질문 댓글 생성 성공")
    void createComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question question = createTestQuestion(1, "질문 제목", "질문 내용", user);
        CreateCommentRequest request = new CreateCommentRequest(1, "댓글 내용");

        QuestionComment questionComment = createTestComment(10, user, question);
        CommentResponse expectedResponse = new CommentResponse(10);

        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));
        when(questionMapper.toQuestionComment(request, user, question))
                .thenReturn(questionComment);
        when(questionCommentJpaRepository.save(any(QuestionComment.class)))
                .thenReturn(questionComment);
        when(questionMapper.toCommentResponse(questionComment))
                .thenReturn(expectedResponse);

        // when
        CommentResponse response = questionCommentService.createComment(user, request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(questionJpaRepository).findByIdAndState(1, ACTIVE);
        verify(questionCommentJpaRepository).save(questionComment);
    }

    @Test
    @DisplayName("존재하지 않는 질문에 댓글 생성 시 예외 발생")
    void createComment_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        CreateCommentRequest request = new CreateCommentRequest(999, "댓글 내용");

        when(questionJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.createComment(user, request));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionJpaRepository).findByIdAndState(999, ACTIVE);
        verify(questionCommentJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("질문 댓글 수정 성공")
    void updateComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 댓글 내용");
        QuestionComment originalComment = createTestQuestionComment(commentIdx, "원본 댓글 내용", user);

        CommentResponse expectedResponse = new CommentResponse(commentIdx);

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.of(originalComment));
        when(questionMapper.toCommentResponse(any(QuestionComment.class)))
                .thenReturn(expectedResponse);

        // when
        CommentResponse response = questionCommentService.updateComment(user, commentIdx, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(questionCommentJpaRepository).findByIdAndState(commentIdx, ACTIVE);
        verify(questionCommentJpaRepository).save(any(QuestionComment.class));
        verify(questionMapper).toCommentResponse(any(QuestionComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 댓글 수정 시도시 예외 발생")
    void updateComment_CommentNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 999;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 댓글 내용");

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.updateComment(user, commentIdx, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("권한 없는 사용자의 댓글 수정 시도시 예외 발생")
    void updateComment_Unauthorized_ThrowsException() {
        // given
        User originalAuthor = createTestUser(1, "작성자", Role.USER);
        User unauthorizedUser = createTestUser(2, "다른사용자", Role.USER);
        Integer commentIdx = 1;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 댓글 내용");
        QuestionComment originalComment = createTestQuestionComment(commentIdx, "원본 댓글 내용", originalAuthor);

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.of(originalComment));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.updateComment(unauthorizedUser, commentIdx, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_UPDATE_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("질문 댓글 삭제 성공")
    void deleteComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        Question question = createTestQuestion(1, "테스트 질문", "테스트 내용", user);
        QuestionComment comment = createTestQuestionComment(commentIdx, "테스트 댓글", user);
        comment.setQuestion(question);
        CommentResponse expectedResponse = new CommentResponse(commentIdx);

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.of(comment));
        when(questionReplyCommentJpaRepository.existsByQuestionCommentAndState(comment, ACTIVE))
                .thenReturn(false);
        when(questionMapper.toCommentResponse(any(QuestionComment.class)))
                .thenReturn(expectedResponse);

        // when
        CommentResponse response = questionCommentService.deleteComment(user, commentIdx);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(questionCommentJpaRepository).findByIdAndState(commentIdx, ACTIVE);
        verify(questionCommentJpaRepository).save(any(QuestionComment.class));
        verify(questionMapper).toCommentResponse(any(QuestionComment.class));
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 시도시 예외 발생")
    void deleteComment_CommentNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 999;

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.deleteComment(user, commentIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("권한 없는 사용자의 댓글 삭제 시도시 예외 발생")
    void deleteComment_Unauthorized_ThrowsException() {
        // given
        User originalAuthor = createTestUser(1, "작성자", Role.USER);
        User unauthorizedUser = createTestUser(2, "다른사용자", Role.USER);
        Integer commentIdx = 1;
        QuestionComment comment = createTestQuestionComment(commentIdx, "테스트 댓글", originalAuthor);

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.of(comment));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.deleteComment(unauthorizedUser, commentIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_DELETE_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("이미 삭제된 댓글을 삭제하려고 시도 시 예외 발생")
    void deleteComment_AlreadyDeleted_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        QuestionComment comment = createTestQuestionComment(commentIdx, "삭제된 댓글", user);
        comment.setDeletedAt();

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.of(comment));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.deleteComment(user, commentIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_ALREADY_DELETED.getMessage());
    }

    @Test
    @DisplayName("질문 댓글 답글 생성 성공")
    void createReplyComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        Question question = createTestQuestion(1, "테스트 질문", "테스트 내용", user);
        QuestionComment comment = createTestQuestionComment(commentIdx, "테스트 댓글", user);
        comment.setQuestion(question);
        CreateReplyCommentRequest request = new CreateReplyCommentRequest(
                commentIdx,
                "테스트 답글 내용"
        );
        QuestionReplyComment replyComment = createTestQuestionReplyComment(1, "테스트 답글 내용", user, comment);
        ReplyCommentResponse expectedResponse = new ReplyCommentResponse(1);

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE))
                .thenReturn(Optional.of(comment));
        when(questionMapper.toQuestionReplyComment(request, user, comment))
                .thenReturn(replyComment);
        when(questionReplyCommentJpaRepository.save(any(QuestionReplyComment.class)))
                .thenReturn(replyComment);
        when(questionMapper.toReplyCommentResponse(replyComment))
                .thenReturn(expectedResponse);

        // when
        ReplyCommentResponse response = questionCommentService.createReplyComment(user, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(expectedResponse);

        verify(questionCommentJpaRepository).findByIdAndState(commentIdx, ACTIVE);
        verify(questionReplyCommentJpaRepository).save(replyComment);
        verify(questionMapper).toReplyCommentResponse(replyComment);
    }

    @Test
    @DisplayName("존재하지 않는 댓글에 답글 생성 시 예외 발생")
    void createReplyComment_CommentNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer invalidCommentIdx = 999;
        CreateReplyCommentRequest request = new CreateReplyCommentRequest(
                invalidCommentIdx,
                "테스트 답글 내용"
        );

        // mocking
        when(questionCommentJpaRepository.findByIdAndState(invalidCommentIdx, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.createReplyComment(user, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("대댓글 수정 성공")
    void updateReplyComment_Success() {
        // given
        Integer replyCommentIdx = 1;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 대댓글 내용");
        QuestionReplyComment replyComment = createTestReplyComment(replyCommentIdx, "원본 대댓글 내용");
        ReplyCommentResponse expectedResponse = new ReplyCommentResponse(replyCommentIdx);

        // Mocking
        when(questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE))
                .thenReturn(Optional.of(replyComment));
        when(questionMapper.toReplyCommentResponse(replyComment))
                .thenReturn(expectedResponse);

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when
        ReplyCommentResponse response = questionCommentService.updateReplyComment(testUser, replyCommentIdx, request);

        // then
        assertThat(response).isEqualTo(expectedResponse);

        // Verify interactions
        verify(questionReplyCommentJpaRepository).findByIdAndState(replyCommentIdx, ACTIVE);
        verify(questionReplyCommentJpaRepository).save(replyComment);
        verify(questionMapper).toReplyCommentResponse(replyComment);
    }

    @Test
    @DisplayName("존재하지 않는 대댓글 수정 시 예외 발생")
    void updateReplyComment_NotFound_ThrowsException() {
        // given
        Integer replyCommentIdx = 999;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 대댓글 내용");

        when(questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE))
                .thenReturn(Optional.empty());

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.updateReplyComment(testUser, replyCommentIdx, request));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_COMMENT_REPLY_NOT_FOUND.getMessage());

        // Verify no interactions with mapper or save
        verify(questionReplyCommentJpaRepository).findByIdAndState(replyCommentIdx, ACTIVE);
        verifyNoInteractions(questionMapper);
    }

    @Test
    @DisplayName("수정 권한 없는 대댓글 수정 시 예외 발생")
    void updateReplyComment_NotAuthorized_ThrowsException() {
        // given
        Integer replyCommentIdx = 1;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 대댓글 내용");
        User anotherUser = createTestUser(2, "다른 사용자", Role.USER);
        QuestionReplyComment replyComment = createTestReplyComment(replyCommentIdx, "원본 대댓글 내용", anotherUser);

        when(questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE))
                .thenReturn(Optional.of(replyComment));

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.updateReplyComment(testUser, replyCommentIdx, request));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED.getMessage());

        // Verify no save or mapping
        verify(questionReplyCommentJpaRepository).findByIdAndState(replyCommentIdx, ACTIVE);
        verifyNoInteractions(questionMapper);
    }

    @Test
    @DisplayName("대댓글 삭제 성공")
    void deleteReplyComment_Success() {
        // given
        Integer replyCommentIdx = 1;
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        QuestionReplyComment replyComment = createTestReplyComment(replyCommentIdx, "대댓글 내용", user);
        ReplyCommentResponse expectedResponse = new ReplyCommentResponse(replyCommentIdx);

        // Mocking
        when(questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE))
                .thenReturn(Optional.of(replyComment));
        when(questionMapper.toReplyCommentResponse(replyComment))
                .thenReturn(expectedResponse);

        // when
        ReplyCommentResponse response = questionCommentService.deleteReplyComment(user, replyCommentIdx);

        // then
        assertThat(response).isEqualTo(expectedResponse);

        // Verify interactions
        verify(questionReplyCommentJpaRepository).findByIdAndState(replyCommentIdx, ACTIVE);
        verify(questionReplyCommentJpaRepository).save(replyComment);
        verify(questionMapper).toReplyCommentResponse(replyComment);
    }

    @Test
    @DisplayName("존재하지 않는 대댓글 삭제 시 예외 발생")
    void deleteReplyComment_NotFound_ThrowsException() {
        // given
        Integer replyCommentIdx = 999;

        when(questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE))
                .thenReturn(Optional.empty());

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.deleteReplyComment(testUser, replyCommentIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_REPLY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("삭제 권한 없는 대댓글 삭제 시 예외 발생")
    void deleteReplyComment_NotAuthorized_ThrowsException() {
        // given
        Integer replyCommentIdx = 1;
        User anotherUser = createTestUser(2, "다른 사용자", Role.USER);
        QuestionReplyComment replyComment = createTestReplyComment(replyCommentIdx, "대댓글 내용", anotherUser);

        when(questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE))
                .thenReturn(Optional.of(replyComment));

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentService.deleteReplyComment(testUser, replyCommentIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_REPLY_DELETE_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("질문 댓글 좋아요 취소 성공")
    void questionCommentLikeCancel_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);

        // 댓글 객체 생성 및 초기화
        QuestionComment comment = createTestQuestionComment(1, "테스트 댓글", createTestUser(2, "작성자", Role.USER));
        comment.setLikeCount(1); // 좋아요 1로 초기화

        // Mock 설정
        when(questionCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(comment));
        when(questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, comment))
                .thenReturn(true); // 이미 좋아요를 누른 상태로 설정

        // when
        String result = questionCommentService.questionCommentLikeCancel(user, request);

        // then
        assertThat(result).isEqualTo("1번 질문 댓글 좋아요 취소 완료");
        assertThat(comment.getLikeCount()).isEqualTo(0); // 좋아요 수가 0이어야 함
        verify(questionCommentLikeJpaRepository).deleteByUserAndQuestionComment(user, comment);
    }



    @Test
    @DisplayName("이미 좋아요한 댓글 좋아요 시도 시 예외 발생")
    void questionCommentLike_AlreadyLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionComment comment = createTestQuestionComment(1, "테스트 댓글", createTestUser(2, "작성자", Role.USER));

        when(questionCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(comment));
        when(questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, comment))
                .thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.questionCommentLike(user, request));
        assertThat(exception.getErrorReason().getMessage()).isEqualTo(ALREADY_LIKE.getMessage());
    }

    @Test
    @DisplayName("좋아요하지 않은 댓글 좋아요 취소 시도 시 예외 발생")
    void questionCommentLikeCancel_NotLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionComment comment = createTestQuestionComment(1, "테스트 댓글", createTestUser(2, "작성자", Role.USER));

        when(questionCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(comment));
        when(questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, comment))
                .thenReturn(false);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.questionCommentLikeCancel(user, request));
        assertThat(exception.getErrorReason().getMessage()).isEqualTo(NOT_LIKE.getMessage());
    }

    @Test
    @DisplayName("자신의 댓글 좋아요 시도 시 예외 발생")
    void questionCommentLike_MyComment_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionComment comment = createTestQuestionComment(1, "테스트 댓글", user);

        when(questionCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(comment));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.questionCommentLike(user, request));
        assertThat(exception.getErrorReason().getMessage()).isEqualTo(MY_COMMENT_LIKE.getMessage());
    }

    @Test
    @DisplayName("질문 대댓글 좋아요 성공")
    void questionReplyCommentLike_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionReplyComment replyComment = createTestReplyComment(1, "대댓글 내용", createTestUser(2, "작성자", Role.USER));

        when(questionReplyCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(replyComment));
        when(questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, replyComment))
                .thenReturn(false);

        // when
        String result = questionCommentService.questionReplyCommentLike(user, request);

        // then
        assertThat(result).isEqualTo("1번 질문 대댓글 좋아요 완료");
        assertThat(replyComment.getLikeCount()).isEqualTo(1);
        verify(questionReplyCommentLikeJpaRepository).save(any());
    }

    @Test
    @DisplayName("이미 좋아요한 대댓글에 다시 좋아요 시도 시 예외 발생")
    void questionReplyCommentLike_AlreadyLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionReplyComment replyComment = createTestReplyComment(1, "대댓글 내용", createTestUser(2, "작성자", Role.USER));

        when(questionReplyCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(replyComment));
        when(questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, replyComment))
                .thenReturn(true);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.questionReplyCommentLike(user, request));
        assertThat(exception.getErrorReason().getMessage()).isEqualTo(ALREADY_LIKE.getMessage());
    }

    @Test
    @DisplayName("질문 대댓글 좋아요 취소 성공")
    void questionReplyCommentLikeCancel_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionReplyComment replyComment = createTestReplyComment(1, "대댓글 내용", createTestUser(2, "작성자", Role.USER));
        replyComment.setLikeCount(1);

        when(questionReplyCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(replyComment));
        when(questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, replyComment))
                .thenReturn(true);

        // when
        String result = questionCommentService.questionReplyCommentLikeCancel(user, request);

        // then
        assertThat(result).isEqualTo("1번 질문 대댓글 좋아요 취소 완료");
        assertThat(replyComment.getLikeCount()).isEqualTo(0);
        verify(questionReplyCommentLikeJpaRepository).deleteByUserAndQuestionReplyComment(user, replyComment);
    }

    @Test
    @DisplayName("좋아요하지 않은 대댓글 좋아요 취소 시도 시 예외 발생")
    void questionReplyCommentLikeCancel_NotLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        QuestionReplyComment replyComment = createTestReplyComment(1, "대댓글 내용", createTestUser(2, "작성자", Role.USER));

        when(questionReplyCommentJpaRepository.findByIdAndState(request.idx(), ACTIVE))
                .thenReturn(Optional.of(replyComment));
        when(questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, replyComment))
                .thenReturn(false);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentService.questionReplyCommentLikeCancel(user, request));
        assertThat(exception.getErrorReason().getMessage()).isEqualTo(NOT_LIKE.getMessage());
    }

    private User createTestUser(Integer id, String name, Role role) {
        return User.builder()
                .id(id)
                .name(name)
                .role(role)
                .build();
    }

    private Question createTestQuestion(Integer id, String title, String contents, User user) {
        return Question.builder()
                .id(id)
                .title(title)
                .contents(contents)
                .user(user)
                .commentCount(0)
                .build();
    }

    private QuestionComment createTestComment(Integer commentId, User user, Question question) {
        return  QuestionComment.builder()
                .id(commentId)
                .user(user)
                .question(question)
                .contents("댓글 내용")
                .likeCount(2)
                .replies(new ArrayList<>())
                .build();

    }

    private QuestionComment createTestQuestionComment(Integer id, String contents, User user) {
        return QuestionComment.builder()
                .id(id)
                .contents(contents)
                .user(user)
                .likeCount(0)
                .build();
    }

    private QuestionReplyComment createTestReply(Integer replyId, User user, QuestionComment parentComment) {
        return QuestionReplyComment.builder()
                .id(replyId)
                .user(user)
                .questionComment(parentComment)
                .contents("대댓글 내용")
                .likeCount(1)
                .build();
    }

    private QuestionReplyComment createTestQuestionReplyComment(Integer id, String contents, User user, QuestionComment comment) {
        return QuestionReplyComment.builder()
                .id(id)
                .contents(contents)
                .likeCount(0)
                .user(user)
                .questionComment(comment)
                .build();
    }

    private QuestionReplyComment createTestReplyComment(Integer id, String contents) {
        return QuestionReplyComment.builder()
                .id(id)
                .contents(contents)
                .user(createTestUser(1, "테스트 사용자", Role.USER))
                .build();
    }


    private QuestionReplyComment createTestReplyComment(Integer id, String contents, User user) {
        QuestionComment parentComment = createTestComment(1, user, createTestQuestion(1, "테스트 질문", "테스트 내용", user));
        return QuestionReplyComment.builder()
                .id(id)
                .contents(contents)
                .user(user)
                .questionComment(parentComment) // 부모 댓글 설정
                .likeCount(0)
                .build();
    }

}