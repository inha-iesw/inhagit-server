package inha.git.question.api.controller;

import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.request.*;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.controller.dto.response.ReplyCommentResponse;
import inha.git.question.api.service.QuestionCommentService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.common.code.status.SuccessStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("질문 댓글 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class QuestionCommentControllerTest {

    @InjectMocks
    private QuestionCommentController questionCommentController;

    @Mock
    private QuestionCommentService questionCommentService;



    @Test
    @DisplayName("특정 질문 댓글 + 대댓글 전체 조회 성공")
    void getAllComments_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer questionIdx = 100;

        List<CommentWithRepliesResponse> fakeComments = List.of(
                new CommentWithRepliesResponse(
                        1,
                        "댓글 내용",
                        null,
                        LocalDateTime.now(),
                        3,
                        true,
                        List.of()
                )
        );

        given(questionCommentService.getAllCommentsByQuestionIdx(user, questionIdx))
                .willReturn(fakeComments);

        // when
        BaseResponse<List<CommentWithRepliesResponse>> response =
                questionCommentController.getAllComments(user, questionIdx);

        // then
        assertThat(response.getResult()).isEqualTo(fakeComments);
        assertThat(response.getMessage()).isEqualTo(QUESTION_COMMENT_SEARCH_OK.getMessage());
        verify(questionCommentService).getAllCommentsByQuestionIdx(user, questionIdx);
    }

    @Test
    @DisplayName("존재하지 않는 질문 조회 시 예외 발생")
    void getAllComments_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer invalidQuestionIdx = 999;

        willThrow(new BaseException(QUESTION_NOT_FOUND))
                .given(questionCommentService)
                .getAllCommentsByQuestionIdx(user, invalidQuestionIdx);

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionCommentController.getAllComments(user, invalidQuestionIdx));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionCommentService).getAllCommentsByQuestionIdx(user, invalidQuestionIdx);
    }


    @Test
    @DisplayName("질문 댓글 생성 성공")
    void createComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        CreateCommentRequest request = new CreateCommentRequest(1, "댓글 내용");
        CommentResponse expectedResponse = new CommentResponse(10);

        when(questionCommentService.createComment(any(User.class), any(CreateCommentRequest.class)))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<CommentResponse> response = questionCommentController.createComment(user, request);

        // then
        assertThat(response.getCode()).isEqualTo(QUESTION_COMMENT_CREATE_OK.getCode());
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionCommentService).createComment(user, request);
    }

    @Test
    @DisplayName("존재하지 않는 질문에 댓글 생성 시 예외 발생")
    void createComment_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        CreateCommentRequest request = new CreateCommentRequest(999, "댓글 내용");

        when(questionCommentService.createComment(any(User.class), any(CreateCommentRequest.class)))
                .thenThrow(new BaseException(QUESTION_NOT_FOUND));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentController.createComment(user, request));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionCommentService).createComment(user, request);
    }

    @Test
    @DisplayName("질문 댓글 수정 성공")
    void updateComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 댓글 내용");
        CommentResponse expectedResponse = new CommentResponse(commentIdx);

        // mocking
        when(questionCommentService.updateComment(user, commentIdx, request))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<CommentResponse> response = questionCommentController.updateComment(user, commentIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionCommentService).updateComment(user, commentIdx, request);
    }

    @Test
    @DisplayName("질문 댓글 삭제 성공")
    void deleteComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        CommentResponse expectedResponse = new CommentResponse(commentIdx);

        // mocking
        when(questionCommentService.deleteComment(user, commentIdx))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<CommentResponse> response = questionCommentController.deleteComment(user, commentIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionCommentService).deleteComment(user, commentIdx);
    }

    @Test
    @DisplayName("질문 댓글 답글 생성 성공")
    void createReplyComment_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer commentIdx = 1;
        CreateReplyCommentRequest request = new CreateReplyCommentRequest(
                commentIdx,
                "테스트 답글 내용"
        );
        ReplyCommentResponse expectedResponse = new ReplyCommentResponse(1);

        // mocking
        when(questionCommentService.createReplyComment(user, request))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<ReplyCommentResponse> response = questionCommentController.createReplyComment(user, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionCommentService).createReplyComment(user, request);
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
        when(questionCommentService.createReplyComment(user, request))
                .thenThrow(new BaseException(QUESTION_COMMENT_NOT_FOUND));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionCommentController.createReplyComment(user, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("대댓글 수정 성공")
    void updateReplyComment_Success() {
        // given
        Integer replyCommentIdx = 1;
        UpdateCommentRequest request = new UpdateCommentRequest("수정된 대댓글 내용");
        ReplyCommentResponse expectedResponse = new ReplyCommentResponse(replyCommentIdx);

        // Mocking Service
        when(questionCommentService.updateReplyComment(any(User.class), eq(replyCommentIdx), eq(request)))
                .thenReturn(expectedResponse);

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when
        BaseResponse<ReplyCommentResponse> response = questionCommentController.updateReplyComment(testUser, replyCommentIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);

        // Verify
        verify(questionCommentService).updateReplyComment(any(User.class), eq(replyCommentIdx), eq(request));
    }

    @Test
    @DisplayName("대댓글 삭제 성공")
    void deleteReplyComment_Success() {
        // given
        Integer replyCommentIdx = 1;
        ReplyCommentResponse expectedResponse = new ReplyCommentResponse(replyCommentIdx);

        // Mocking Service
        when(questionCommentService.deleteReplyComment(any(User.class), eq(replyCommentIdx)))
                .thenReturn(expectedResponse);

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        // when
        BaseResponse<ReplyCommentResponse> response = questionCommentController.deleteReplyComment(testUser, replyCommentIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);

        // Verify
        verify(questionCommentService).deleteReplyComment(any(User.class), eq(replyCommentIdx));
    }

    @Test
    @DisplayName("존재하지 않는 대댓글 삭제 시 예외 발생")
    void deleteReplyComment_NotFound_ThrowsException() {
        // given
        Integer replyCommentIdx = 999;

        User testUser = createTestUser(1, "테스트 사용자", Role.USER);

        when(questionCommentService.deleteReplyComment(any(User.class), eq(replyCommentIdx)))
                .thenThrow(new BaseException(QUESTION_COMMENT_REPLY_NOT_FOUND));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentController.deleteReplyComment(testUser, replyCommentIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_COMMENT_REPLY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("질문 댓글 좋아요 성공")
    void questionCommentLike_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        String expectedMessage = "1번 질문 댓글 좋아요 완료";

        when(questionCommentService.questionCommentLike(user, request))
                .thenReturn(expectedMessage);

        // when
        BaseResponse<String> response = questionCommentController.questionCommentLike(user, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedMessage);
        verify(questionCommentService).questionCommentLike(user, request);
    }

    @Test
    @DisplayName("질문 댓글 좋아요 취소 성공")
    void questionCommentLikeCancel_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        String expectedMessage = "1번 질문 댓글 좋아요 취소 완료";

        when(questionCommentService.questionCommentLikeCancel(user, request))
                .thenReturn(expectedMessage);

        // when
        BaseResponse<String> response = questionCommentController.questionCommentLikeCancel(user, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedMessage);
        verify(questionCommentService).questionCommentLikeCancel(user, request);
    }

    @Test
    @DisplayName("질문 대댓글 좋아요 성공")
    void questionReplyCommentLike_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        String expectedMessage = "1번 질문 대댓글 좋아요 완료";

        when(questionCommentService.questionReplyCommentLike(user, request))
                .thenReturn(expectedMessage);

        // when
        BaseResponse<String> response = questionCommentController.questionReplyCommentLike(user, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedMessage);
        assertThat(response.getMessage()).isEqualTo(LIKE_SUCCESS.getMessage());
        verify(questionCommentService).questionReplyCommentLike(user, request);
    }

    @Test
    @DisplayName("이미 좋아요한 대댓글에 좋아요 시도 시 예외 발생")
    void questionReplyCommentLike_AlreadyLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);

        when(questionCommentService.questionReplyCommentLike(user, request))
                .thenThrow(new BaseException(ALREADY_LIKE));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentController.questionReplyCommentLike(user, request));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(ALREADY_LIKE.getMessage());
        verify(questionCommentService).questionReplyCommentLike(user, request);
    }

    @Test
    @DisplayName("질문 대댓글 좋아요 취소 성공")
    void questionReplyCommentLikeCancel_Success() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);
        String expectedMessage = "1번 질문 대댓글 좋아요 취소 완료";

        when(questionCommentService.questionReplyCommentLikeCancel(user, request))
                .thenReturn(expectedMessage);

        // when
        BaseResponse<String> response = questionCommentController.questionReplyCommentLikeCancel(user, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedMessage);
        assertThat(response.getMessage()).isEqualTo(LIKE_CANCEL_SUCCESS.getMessage());
        verify(questionCommentService).questionReplyCommentLikeCancel(user, request);
    }

    @Test
    @DisplayName("좋아요하지 않은 대댓글 좋아요 취소 시도 시 예외 발생")
    void questionReplyCommentLikeCancel_NotLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트 사용자", Role.USER);
        CommentLikeRequest request = new CommentLikeRequest(1);

        when(questionCommentService.questionReplyCommentLikeCancel(user, request))
                .thenThrow(new BaseException(NOT_LIKE));

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionCommentController.questionReplyCommentLikeCancel(user, request));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(NOT_LIKE.getMessage());
        verify(questionCommentService).questionReplyCommentLikeCancel(user, request);
    }


    private User createTestUser(int id, String name, Role role) {
        return User.builder()
                .id(id)
                .name(name)
                .role(role)
                .build();
    }
}