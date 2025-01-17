package inha.git.question.api.controller;

import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.common.BaseResponse;
import inha.git.common.exceptions.BaseException;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.LikeRequest;
import inha.git.question.api.controller.dto.request.SearchQuestionCond;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.controller.dto.response.SearchLikeState;
import inha.git.question.api.controller.dto.response.SearchQuestionResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.api.service.QuestionService;
import inha.git.question.domain.Question;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import inha.git.utils.PagingUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@DisplayName("질문 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    @InjectMocks
    private QuestionController questionController;

    @Mock
    private QuestionService questionService;

    @Test
    @DisplayName("질문 전체 조회 성공")
    void getQuestions_Success() {
        // given
        int page = 1;
        int size = 10;
        List<SearchQuestionsResponse> questions = Arrays.asList(
                new SearchQuestionsResponse(
                        1,
                        "질문1",
                        LocalDateTime.now(),
                        "과목1",
                        new SearchSemesterResponse(1, "2024-1"),
                        new SearchCategoryResponse(1, "카테고리1"),
                        0,
                        0,
                        List.of(new SearchFieldResponse(1, "분야1")),
                        new SearchUserResponse(1, "작성자1", 1)
                ),
                new SearchQuestionsResponse(
                        2,
                        "질문2",
                        LocalDateTime.now(),
                        "과목2",
                        new SearchSemesterResponse(1, "2024-1"),
                        new SearchCategoryResponse(2, "카테고리2"),
                        1,
                        2,
                        List.of(new SearchFieldResponse(2, "분야2")),
                        new SearchUserResponse(2, "작성자2", 1)
                )
        );
        Page<SearchQuestionsResponse> expectedPage = new PageImpl<>(questions);

        given(PagingUtils.toPageIndex(page)).willReturn(0);
        given(questionService.getQuestions(0, 9)).willReturn(expectedPage);

        // when
        BaseResponse<Page<SearchQuestionsResponse>> response = questionController.getQuestions(page, size);

        // then
        assertThat(response.getResult()).isEqualTo(expectedPage);
    }

    @Test
    @DisplayName("질문 조건 검색 성공")
    void getCondQuestions_Success() {
        // given
        int page = 1;
        int size = 10;
        SearchQuestionCond searchQuestionCond = new SearchQuestionCond(
                1, 1, 1, 1, 1, "알고리즘", "정렬"
        );

        List<SearchQuestionsResponse> questions = Arrays.asList(
                new SearchQuestionsResponse(
                        1,
                        "정렬 알고리즘 질문",
                        LocalDateTime.now(),
                        "알고리즘",
                        new SearchSemesterResponse(1, "2024-1"),
                        new SearchCategoryResponse(1, "CS"),
                        0,
                        0,
                        List.of(new SearchFieldResponse(1, "알고리즘")),
                        new SearchUserResponse(1, "작성자1", 1)
                )
        );
        Page<SearchQuestionsResponse> expectedPage = new PageImpl<>(questions);

        given(PagingUtils.toPageIndex(page)).willReturn(0);
        given(questionService.getCondQuestions(searchQuestionCond, 0, 9))
                .willReturn(expectedPage);

        // when
        BaseResponse<Page<SearchQuestionsResponse>> response =
                questionController.getCondQuestions(page, size, searchQuestionCond);

        // then
        assertThat(response.getResult()).isEqualTo(expectedPage);
        verify(questionService).getCondQuestions(searchQuestionCond, 0, 9);
    }

    @Test
    @DisplayName("잘못된 페이지 번호로 조회 시 예외 발생")
    void getQuestions_WithInvalidPage_ThrowsException() {
        // given
        Integer invalidPage = 0;
        Integer size = 10;

        doThrow(new BaseException(INVALID_PAGE))
                .when(pagingUtils).validatePage(invalidPage);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionController.getQuestions(invalidPage, size));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(INVALID_PAGE.getMessage());
    }

    @Test
    @DisplayName("질문 조건 검색 - 잘못된 페이지 번호")
    void getCondQuestions_WithInvalidPage_ThrowsException() {
        // given
        Integer invalidPage = 0;
        Integer size = 10;
        SearchQuestionCond searchQuestionCond = new SearchQuestionCond(
                1, 1, 1, 1, 1, "알고리즘", "정렬"
        );

        doThrow(new BaseException(INVALID_PAGE))
                .when(pagingUtils).validatePage(invalidPage);

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionController.getCondQuestions(invalidPage, size, searchQuestionCond));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(INVALID_PAGE.getMessage());
    }

    @Test
    @DisplayName("질문 상세 조회 성공")
    void getQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer questionIdx = 1;
        SearchQuestionResponse expectedResponse = createSearchQuestionResponse();

        when(questionService.getQuestion(user, questionIdx))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<SearchQuestionResponse> response = questionController.getQuestion(user, questionIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionService).getQuestion(user, questionIdx);
    }

    @Test
    @DisplayName("존재하지 않는 질문 조회시 예외 발생")
    void getQuestion_NotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer invalidQuestionIdx = 999;

        when(questionService.getQuestion(user, invalidQuestionIdx))
                .thenThrow(new BaseException(QUESTION_NOT_FOUND));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.getQuestion(user, invalidQuestionIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("질문 생성 성공")
    void createQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        CreateQuestionRequest request = new CreateQuestionRequest(
                "질문 제목",
                "질문 내용",
                "알고리즘",
                List.of(1),
                1
        );
        QuestionResponse expectedResponse = new QuestionResponse(1);

        when(questionService.createQuestion(user, request))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<QuestionResponse> response = questionController.createQuestion(user, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionService).createQuestion(user, request);
    }

    @Test
    @DisplayName("기업 회원의 질문 생성 시도시 예외 발생")
    void createQuestion_CompanyUser_ThrowsException() {
        // given
        User companyUser = createTestUser(1, "기업회원", Role.COMPANY);
        CreateQuestionRequest request = new CreateQuestionRequest(
                "질문 제목",
                "질문 내용",
                "알고리즘",
                List.of(1),
                1
        );

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.createQuestion(companyUser, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(COMPANY_CANNOT_CREATE_QUESTION.getMessage());
        verify(questionService, never()).createQuestion(any(), any());
    }

    @Test
    @DisplayName("질문 수정 성공")
    void updateQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer questionIdx = 1;
        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "수정된 제목",
                "수정된 내용",
                "수정된 주제",
                List.of(1, 2),
                1
        );
        QuestionResponse expectedResponse = new QuestionResponse(1);

        when(questionService.updateQuestion(user, questionIdx, request))
                .thenReturn(expectedResponse);

        // when
        BaseResponse<QuestionResponse> response =
                questionController.updateQuestion(user, questionIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionService).updateQuestion(user, questionIdx, request);
    }

    @Test
    @DisplayName("질문 삭제 성공")
    void deleteQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer questionIdx = 1;
        QuestionResponse expectedResponse = new QuestionResponse(1);

        when(questionService.deleteQuestion(user, questionIdx)).thenReturn(expectedResponse);

        // when
        BaseResponse<QuestionResponse> response = questionController.deleteQuestion(user, questionIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(questionService).deleteQuestion(user, questionIdx);
    }

    @Test
    @DisplayName("존재하지 않는 질문 삭제 시도시 예외 발생")
    void deleteQuestion_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer invalidQuestionIdx = 999;

        when(questionService.deleteQuestion(user, invalidQuestionIdx))
                .thenThrow(new BaseException(QUESTION_NOT_FOUND));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.deleteQuestion(user, invalidQuestionIdx));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionService).deleteQuestion(user, invalidQuestionIdx);
    }

    @Test
    @DisplayName("권한 없는 사용자가 질문 삭제 시도시 예외 발생")
    void deleteQuestion_UnauthorizedUser_ThrowsException() {
        // given
        User unauthorizedUser = createTestUser(2, "다른 사용자", Role.USER);
        Integer questionIdx = 1;

        when(questionService.deleteQuestion(unauthorizedUser, questionIdx))
                .thenThrow(new BaseException(QUESTION_DELETE_NOT_AUTHORIZED));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.deleteQuestion(unauthorizedUser, questionIdx));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_DELETE_NOT_AUTHORIZED.getMessage());
        verify(questionService).deleteQuestion(unauthorizedUser, questionIdx);
    }

    @Test
    @DisplayName("관리자가 다른 유저의 질문 삭제 성공")
    void deleteQuestion_AsAdmin_Success() {
        // given
        User admin = createTestUser(1, "관리자", Role.ADMIN);
        User otherUser = createTestUser(2, "다른유저", Role.USER);
        createTestQuestion(1, "질문 제목", "질문 내용", otherUser);

        when(questionService.deleteQuestion(admin, 1))
                .thenReturn(new QuestionResponse(1));

        // when
        BaseResponse<QuestionResponse> response = questionController.deleteQuestion(admin, 1);

        // then
        assertThat(response.getResult().idx()).isEqualTo(1);
        verify(questionService).deleteQuestion(admin, 1);
    }

    @Test
    @DisplayName("질문 좋아요 성공")
    void questionLike_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(100);  // 질문 ID 100

        given(questionService.createQuestionLike(user, likeRequest))
                .willReturn("100번 질문 좋아요 완료");

        // when
        BaseResponse<String> response = questionController.questionLike(user, likeRequest);

        // then
        assertThat(response.getResult()).isEqualTo("100번 질문 좋아요 완료");
        verify(questionService).createQuestionLike(user, likeRequest);
    }

    @Test
    @DisplayName("질문 좋아요 - 내 질문 좋아요 시도 시 예외 발생")
    void questionLike_MyQuestion_ThrowsException() {
        // given
        User user = createTestUser(1, "내질문", Role.USER);
        LikeRequest likeRequest = new LikeRequest(100);

        willThrow(new BaseException(MY_QUESTION_LIKE))
                .given(questionService).createQuestionLike(user, likeRequest);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.questionLike(user, likeRequest));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(MY_QUESTION_LIKE.getMessage());
        verify(questionService).createQuestionLike(user, likeRequest);
    }

    @Test
    @DisplayName("질문 좋아요 - 이미 좋아요 한 질문 예외 발생")
    void questionLike_AlreadyLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(100);

        willThrow(new BaseException(QUESTION_ALREADY_LIKE))
                .given(questionService).createQuestionLike(user, likeRequest);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.questionLike(user, likeRequest));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_ALREADY_LIKE.getMessage());
        verify(questionService).createQuestionLike(user, likeRequest);
    }

    @Test
    @DisplayName("질문 좋아요 - 질문 없음 예외 발생")
    void questionLike_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(999);

        willThrow(new BaseException(QUESTION_NOT_FOUND))
                .given(questionService).createQuestionLike(user, likeRequest);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.questionLike(user, likeRequest));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionService).createQuestionLike(user, likeRequest);
    }


    @Test
    @DisplayName("질문 좋아요 취소 성공")
    void questionLikeCancel_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(200); // 질문 ID 200

        given(questionService.questionLikeCancel(user, likeRequest))
                .willReturn("200번 프로젝트 좋아요 취소 완료");

        // when
        BaseResponse<String> response = questionController.questionLikeCancel(user, likeRequest);

        // then
        assertThat(response.getResult()).isEqualTo("200번 프로젝트 좋아요 취소 완료");
        verify(questionService).questionLikeCancel(user, likeRequest);
    }

    @Test
    @DisplayName("질문 좋아요 취소 - 내 질문 예외 발생")
    void questionLikeCancel_MyQuestion_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(200);

        willThrow(new BaseException(MY_QUESTION_LIKE))
                .given(questionService).questionLikeCancel(user, likeRequest);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.questionLikeCancel(user, likeRequest));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(MY_QUESTION_LIKE.getMessage());
        verify(questionService).questionLikeCancel(user, likeRequest);
    }

    @Test
    @DisplayName("질문 좋아요 취소 - 좋아요하지 않은 질문 예외 발생")
    void questionLikeCancel_NotLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(200);

        willThrow(new BaseException(QUESTION_NOT_LIKE))
                .given(questionService).questionLikeCancel(user, likeRequest);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.questionLikeCancel(user, likeRequest));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_NOT_LIKE.getMessage());
        verify(questionService).questionLikeCancel(user, likeRequest);
    }

    @Test
    @DisplayName("질문 좋아요 취소 - 질문 없음 예외 발생")
    void questionLikeCancel_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        LikeRequest likeRequest = new LikeRequest(999);

        willThrow(new BaseException(QUESTION_NOT_FOUND))
                .given(questionService).questionLikeCancel(user, likeRequest);

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionController.questionLikeCancel(user, likeRequest));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionService).questionLikeCancel(user, likeRequest);
    }

    private Question createTestQuestion(Integer id , String title, String contents, User user) {
        return Question.builder()
                .id(id)
                .title(title)
                .contents(contents)
                .user(user)
                .build();
    }

    private User createTestUser(Integer id, String name, Role role) {
        return User.builder()
                .id(id)
                .name(name)
                .role(role)
                .build();
    }

    private SearchQuestionResponse createSearchQuestionResponse() {
        return new SearchQuestionResponse(
                1,                                             // idx
                "질문 제목",                                    // title
                "질문 내용",                                    // contents
                LocalDateTime.now(),                           // createdAt
                new SearchLikeState(false),                    // likeState
                0,                                            // likeCount
                "알고리즘",                                     // subject
                List.of(new SearchFieldResponse(1, "알고리즘")), // fieldList
                new SearchUserResponse(1, "테스트유저", 1),      // author
                new SearchSemesterResponse(1, "2024-1")        // semester
        );
    }

}