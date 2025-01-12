package inha.git.question.api.service;

import inha.git.category.controller.dto.response.SearchCategoryResponse;
import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.question.api.controller.dto.request.LikeRequest;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.user.domain.enums.Role;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.id.QuestionFieldId;
import inha.git.mapping.domain.repository.QuestionFieldJpaRepository;
import inha.git.mapping.domain.repository.QuestionLikeJpaRepository;
import inha.git.project.api.controller.dto.response.SearchFieldResponse;
import inha.git.project.api.controller.dto.response.SearchUserResponse;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.SearchQuestionCond;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.controller.dto.response.SearchLikeState;
import inha.git.question.api.controller.dto.response.SearchQuestionResponse;
import inha.git.question.api.controller.dto.response.SearchQuestionsResponse;
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.question.domain.repository.QuestionQueryRepository;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.semester.mapper.SemesterMapper;
import inha.git.statistics.api.service.StatisticsServiceImpl;
import inha.git.user.domain.User;
import inha.git.utils.IdempotentProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.Constant.CURRICULUM;
import static inha.git.common.code.status.ErrorStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("질문 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @InjectMocks
    private QuestionServiceImpl questionService;

    @Mock
    private QuestionQueryRepository questionQueryRepository;

    @Mock
    private QuestionJpaRepository questionJpaRepository;

    @Mock
    private QuestionMapper questionMapper;

    @Mock
    private QuestionLikeJpaRepository questionLikeJpaRepository;

    @Mock
    private QuestionFieldJpaRepository questionFieldJpaRepository;

    @Mock
    private FieldJpaRepository fieldJpaRepository;

    @Mock
    private SemesterJpaRepository semesterJpaRepository;

    @Mock
    private CategoryJpaRepository categoryJpaRepository;

    @Mock
    private StatisticsServiceImpl statisticsService;

    @Mock
    private IdempotentProvider idempotentProvider;

    @Mock
    private SemesterMapper semesterMapper;

    @Test
    @DisplayName("질문 페이징 조회 성공")
    void getQuestions_Success() {
        // given
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));

        List<SearchQuestionsResponse> questions = Arrays.asList(
                new SearchQuestionsResponse(
                        1,
                        "질문1",
                        LocalDateTime.now(),
                        "과목1",
                        new SearchSemesterResponse(1, "학기1"),
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
                        new SearchSemesterResponse(2, "학기2"),
                        new SearchCategoryResponse(2, "카테고리2"),
                        1,
                        2,
                        List.of(new SearchFieldResponse(2, "분야2")),
                        new SearchUserResponse(2, "작성자2", 1)
                )
        );

        Page<SearchQuestionsResponse> expectedPage = new PageImpl<>(questions);

        given(questionQueryRepository.getQuestions(pageable))
                .willReturn(expectedPage);

        // when
        Page<SearchQuestionsResponse> result = questionService.getQuestions(page, size);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(questionQueryRepository).getQuestions(pageable);
    }

    @Test
    @DisplayName("조건 검색 - 모든 조건이 있는 경우")
    void getCondQuestions_WithAllConditions_Success() {
        // given
        int page = 0;
        int size = 10;
        SearchQuestionCond searchQuestionCond = new SearchQuestionCond(
                1,  // collegeIdx
                1,  // departmentIdx
                1,  // semesterIdx
                1,  // categoryIdx
                1,  // fieldIdx
                "알고리즘",  // subject
                "정렬"   // title
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
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
                        new SearchUserResponse(1, "작성자1",1)
                )
        );
        Page<SearchQuestionsResponse> expectedPage = new PageImpl<>(questions);

        given(questionQueryRepository.getCondQuestions(searchQuestionCond, pageable))
                .willReturn(expectedPage);

        // when
        Page<SearchQuestionsResponse> result = questionService.getCondQuestions(
                searchQuestionCond, page, size);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(questionQueryRepository).getCondQuestions(searchQuestionCond, pageable);
    }

    @Test
    @DisplayName("조건 검색 - 일부 조건만 있는 경우")
    void getCondQuestions_WithPartialConditions_Success() {
        // given
        int page = 0;
        int size = 10;
        SearchQuestionCond searchQuestionCond = new SearchQuestionCond(
                null,  // collegeIdx
                null,  // departmentIdx
                1,     // semesterIdx
                null,  // categoryIdx
                null,  // fieldIdx
                "알고리즘",  // subject
                null   // title
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        List<SearchQuestionsResponse> questions = Arrays.asList(
                new SearchQuestionsResponse(
                        1,
                        "알고리즘 질문1",
                        LocalDateTime.now(),
                        "알고리즘",
                        new SearchSemesterResponse(1, "2024-1"),
                        new SearchCategoryResponse(1, "CS"),
                        0,
                        0,
                        List.of(new SearchFieldResponse(1, "알고리즘")),
                        new SearchUserResponse(1, "작성자1", 1)
                ),
                new SearchQuestionsResponse(
                        2,
                        "알고리즘 질문2",
                        LocalDateTime.now(),
                        "알고리즘",
                        new SearchSemesterResponse(1, "2024-1"),
                        new SearchCategoryResponse(2, "AI"),
                        0,
                        0,
                        List.of(new SearchFieldResponse(2, "머신러닝")),
                        new SearchUserResponse(2, "작성자2", 1)
                )
        );
        Page<SearchQuestionsResponse> expectedPage = new PageImpl<>(questions);

        given(questionQueryRepository.getCondQuestions(searchQuestionCond, pageable))
                .willReturn(expectedPage);

        // when
        Page<SearchQuestionsResponse> result = questionService.getCondQuestions(
                searchQuestionCond, page, size);

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(questionQueryRepository).getCondQuestions(searchQuestionCond, pageable);
    }

    @Test
    @DisplayName("조건 검색 - 검색 결과가 없는 경우")
    void getCondQuestions_NoResults_Success() {
        // given
        int page = 0;
        int size = 10;
        SearchQuestionCond searchQuestionCond = new SearchQuestionCond(
                1, 1, 1, 1, 1, "존재하지않는과목", "존재하지않는제목"
        );

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        Page<SearchQuestionsResponse> expectedPage = new PageImpl<>(Collections.emptyList());

        given(questionQueryRepository.getCondQuestions(searchQuestionCond, pageable))
                .willReturn(expectedPage);

        // when
        Page<SearchQuestionsResponse> result = questionService.getCondQuestions(
                searchQuestionCond, page, size);

        // then
        assertThat(result).isEqualTo(expectedPage);
        assertThat(result.getContent()).isEmpty();
        verify(questionQueryRepository).getCondQuestions(searchQuestionCond, pageable);
    }

    @Test
    @DisplayName("질문 상세 조회 성공")
    void getQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question question = createTestQuestion(1, "질문 제목", "질문 내용", user);
        Semester semester = createTestSemester(1, "2024-1");
        question.setSemester(semester);

        // 각 매퍼의 반환값 설정
        SearchSemesterResponse semesterResponse = new SearchSemesterResponse(1, "2024-1");
        SearchUserResponse userResponse = new SearchUserResponse(1, "테스트유저", 1);
        SearchLikeState likeState = new SearchLikeState(false);

        Field field1 = createTestField(1, "알고리즘");
        Field field2 = createTestField(2, "자료구조");
        List<QuestionField> questionFields = List.of(
                createTestQuestionField(1, question, field1),
                createTestQuestionField(2, question, field2)
        );

        List<SearchFieldResponse> fieldResponses = List.of(
                new SearchFieldResponse(1, "알고리즘"),
                new SearchFieldResponse(2, "자료구조")
        );

        SearchQuestionResponse expectedResponse = new SearchQuestionResponse(
                1,
                "질문 제목",
                "질문 내용",
                question.getCreatedAt(),
                likeState,
                0,
                "알고리즘",
                fieldResponses,
                userResponse,
                semesterResponse
        );

        // 각 메서드 호출에 대한 동작 설정
        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));

        when(semesterMapper.semesterToSearchSemesterResponse(question.getSemester()))
                .thenReturn(semesterResponse);

        when(questionMapper.userToSearchUserResponse(question.getUser()))
                .thenReturn(userResponse);

        when(questionLikeJpaRepository.existsByUserAndQuestion(user, question))
                .thenReturn(false);

        when(questionMapper.questionToSearchLikeState(false))
                .thenReturn(likeState);

        when(questionFieldJpaRepository.findByQuestion(question))
                .thenReturn(questionFields);

        when(questionMapper.projectFieldToSearchFieldResponse(field1))
                .thenReturn(new SearchFieldResponse(1, "알고리즘"));
        when(questionMapper.projectFieldToSearchFieldResponse(field2))
                .thenReturn(new SearchFieldResponse(2, "자료구조"));

        when(questionMapper.questionToSearchQuestionResponse(
                eq(question),
                eq(fieldResponses),
                eq(userResponse),
                eq(semesterResponse),
                eq(likeState)))
                .thenReturn(expectedResponse);

        // when
        SearchQuestionResponse response = questionService.getQuestion(user, 1);

        // then
        assertThat(response)
                .isNotNull()
                .isEqualTo(expectedResponse);

        // 모든 메서드 호출 검증
        verify(questionJpaRepository).findByIdAndState(1, ACTIVE);
        verify(semesterMapper).semesterToSearchSemesterResponse(question.getSemester());
        verify(questionMapper).userToSearchUserResponse(question.getUser());
        verify(questionLikeJpaRepository).existsByUserAndQuestion(user, question);
        verify(questionMapper).questionToSearchLikeState(false);
        verify(questionFieldJpaRepository).findByQuestion(question);
        verify(questionMapper).questionToSearchQuestionResponse(
                eq(question),
                eq(fieldResponses),
                eq(userResponse),
                eq(semesterResponse),
                eq(likeState)
        );
    }

    @Test
    @DisplayName("존재하지 않는 질문 조회 시 예외 발생")
    void getQuestion_NotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Integer nonExistentQuestionId = 999;

        given(questionJpaRepository.findByIdAndState(nonExistentQuestionId, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                questionService.getQuestion(user, nonExistentQuestionId));

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

        Semester semester = createTestSemester(1, "2024-1");
        Category category = createTestCategory(1, "커리큘럼");
        Question question = createTestQuestion(1, "질문 제목", "질문 내용", user);
        Field field = createTestField(1, "알고리즘");
        QuestionField questionField = createTestQuestionField(1, question, field);

        // 학기, 카테고리 조회
        when(semesterJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(semester));
        when(categoryJpaRepository.findByNameAndState(CURRICULUM, ACTIVE))
                .thenReturn(Optional.of(category));

        // 질문 생성
        when(questionMapper.createQuestionRequestToQuestion(request, user, semester, category))
                .thenReturn(question);
        when(questionJpaRepository.save(question))
                .thenReturn(question);

        // 필드 관련 처리
        when(fieldJpaRepository.findAllById(List.of(1)))
                .thenReturn(List.of(field));
        when(fieldJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(field));
        when(questionMapper.createQuestionField(any(Question.class), any(Field.class)))
                .thenReturn(questionField);

        // 응답 변환
        when(questionMapper.questionToQuestionResponse(question))
                .thenReturn(new QuestionResponse(1));

        // when
        QuestionResponse response = questionService.createQuestion(user, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.idx()).isEqualTo(1);

        verify(questionJpaRepository).save(any(Question.class));
        verify(questionFieldJpaRepository).saveAll(anyList());
        verify(statisticsService).adjustCount(eq(user), anyList(), eq(semester), eq(category), eq(2), true);
        verify(fieldJpaRepository).findByIdAndState(eq(1), eq(ACTIVE));
    }

    @Test
    @DisplayName("존재하지 않는 학기로 질문 생성 시 예외 발생")
    void createQuestion_WithInvalidSemester_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        CreateQuestionRequest request = new CreateQuestionRequest(
                "질문 제목",
                "질문 내용",
                "알고리즘",
                List.of(1),
                999  // 존재하지 않는 학기 ID
        );

        when(semesterJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.createQuestion(user, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(SEMESTER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("카테고리를 찾을 수 없을 때 예외 발생")
    void createQuestion_CategoryNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        CreateQuestionRequest request = new CreateQuestionRequest(
                "질문 제목",
                "질문 내용",
                "알고리즘",
                List.of(1),
                1
        );

        Semester semester = createTestSemester(1, "2024-1");

        when(semesterJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(semester));
        when(categoryJpaRepository.findByNameAndState(CURRICULUM, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.createQuestion(user, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(CATEGORY_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("질문 수정 성공")
    void updateQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question originalQuestion = createTestQuestion(1, "원본 제목", "원본 내용", user);

        Semester originalSemester = createTestSemester(1, "2024-1");
        Semester newSemester = createTestSemester(2, "2024-2");
        Category category = createTestCategory(1, "커리큘럼");

        Field originalField = createTestField(1, "알고리즘");
        Field newField = createTestField(2, "자료구조");
        QuestionField originalQuestionField = createTestQuestionField(1, originalQuestion, originalField);

        originalQuestion.setSemester(originalSemester);
        originalQuestion.setCategory(category);
        originalQuestion.setQuestionFields(new ArrayList<>(List.of(originalQuestionField)));

        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "수정된 제목",
                "수정된 내용",
                "수정된 주제",
                List.of(2),  // 새로운 필드 ID
                2  // 새로운 학기 ID
        );

        // mocking
        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(originalQuestion));
        when(semesterJpaRepository.findByIdAndState(2, ACTIVE))
                .thenReturn(Optional.of(newSemester));
        when(fieldJpaRepository.findAllById(List.of(2)))
                .thenReturn(List.of(newField));
        when(fieldJpaRepository.findById(2))
                .thenReturn(Optional.of(newField));
        when(questionJpaRepository.save(any(Question.class)))
                .thenReturn(originalQuestion);
        when(questionMapper.questionToQuestionResponse(any(Question.class)))
                .thenReturn(new QuestionResponse(1));

        // when
        QuestionResponse response = questionService.updateQuestion(user, 1, request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.idx()).isEqualTo(1);

        verify(questionJpaRepository).save(any(Question.class));
        verify(statisticsService).adjustCount(eq(user), anyList(), eq(originalSemester), eq(category), eq(2), false);
        verify(statisticsService).adjustCount(eq(user), anyList(), eq(newSemester), eq(category), eq(2), true);
    }

    @Test
    @DisplayName("존재하지 않는 질문 수정 시도시 예외 발생")
    void updateQuestion_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "수정된 제목",
                "수정된 내용",
                "수정된 주제",
                List.of(1),
                1
        );

        when(questionJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.updateQuestion(user, 999, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("권한 없는 사용자의 질문 수정 시도시 예외 발생")
    void updateQuestion_Unauthorized_ThrowsException() {
        // given
        User originalAuthor = createTestUser(1, "작성자", Role.USER);
        User unauthorizedUser = createTestUser(2, "다른사용자", Role.USER);
        Question question = createTestQuestion(1, "원본 제목", "원본 내용", originalAuthor);

        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "수정된 제목",
                "수정된 내용",
                "수정된 주제",
                List.of(1),
                1
        );

        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.updateQuestion(unauthorizedUser, 1, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(QUESTION_NOT_AUTHORIZED.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 학기로 수정 시도시 예외 발생")
    void updateQuestion_SemesterNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question question = createTestQuestion(1, "원본 제목", "원본 내용", user);

        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "수정된 제목",
                "수정된 내용",
                "수정된 주제",
                List.of(1),
                999  // 존재하지 않는 학기 ID
        );

        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));
        when(semesterJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.updateQuestion(user, 1, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(SEMESTER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 필드로 수정 시도시 예외 발생")
    void updateQuestion_FieldNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question question = createTestQuestion(1, "원본 제목", "원본 내용", user);
        Semester semester = createTestSemester(1, "2024-1");

        UpdateQuestionRequest request = new UpdateQuestionRequest(
                "수정된 제목",
                "수정된 내용",
                "수정된 주제",
                List.of(999),  // 존재하지 않는 필드 ID
                1
        );

        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));
        when(semesterJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(semester));
        when(fieldJpaRepository.findById(999))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.updateQuestion(user, 1, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(FIELD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("질문 삭제 성공")
    void deleteQuestion_Success() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);
        Question question = createTestQuestion(1, "질문 제목", "질문 내용", user);

        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));
        when(questionMapper.questionToQuestionResponse(question))
                .thenReturn(new QuestionResponse(1));

        // when
        QuestionResponse response = questionService.deleteQuestion(user, 1);

        // then
        assertThat(response).isNotNull();
        assertThat(response.idx()).isEqualTo(1);

        verify(questionJpaRepository).findByIdAndState(1, ACTIVE);
        verify(statisticsService).adjustCount(eq(user), anyList(), eq(question.getSemester()), eq(question.getCategory()), eq(2), false);
        verify(questionJpaRepository).save(question);
    }

    @Test
    @DisplayName("존재하지 않는 질문 삭제 시도시 예외 발생")
    void deleteQuestion_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "테스트유저", Role.USER);

        when(questionJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.deleteQuestion(user, 999));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("권한 없는 사용자가 질문 삭제 시도시 예외 발생")
    void deleteQuestion_UnauthorizedUser_ThrowsException() {
        // given
        User originalAuthor = createTestUser(1, "작성자", Role.USER);
        User unauthorizedUser = createTestUser(2, "다른 사용자", Role.USER);
        Question question = createTestQuestion(1, "질문 제목", "질문 내용", originalAuthor);

        when(questionJpaRepository.findByIdAndState(1, ACTIVE))
                .thenReturn(Optional.of(question));

        // when & then
        BaseException exception = assertThrows(BaseException.class,
                () -> questionService.deleteQuestion(unauthorizedUser, 1));

        assertThat(exception.getErrorReason().getMessage()).isEqualTo(QUESTION_DELETE_NOT_AUTHORIZED.getMessage());
    }



    @Test
    @DisplayName("질문 좋아요 성공")
    void createQuestionLike_Success() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        LikeRequest likeRequest = new LikeRequest(100);
        Question question = createTestQuestion(100, 999, 0);

        when(questionJpaRepository.findByIdAndState(100, ACTIVE))
                .thenReturn(Optional.of(question));
        when(questionLikeJpaRepository.existsByUserAndQuestion(user, question))
                .thenReturn(false);

        when(questionMapper.createQuestionLike(user, question))
                .thenReturn(null);

        // when
        String result = questionService.createQuestionLike(user, likeRequest);

        // then
        assertThat(result).isEqualTo("100번 질문 좋아요 완료");
        assertThat(question.getLikeCount()).isEqualTo(1);
        verify(questionLikeJpaRepository).save(any());
    }

    @Test
    @DisplayName("질문 좋아요 - 내 질문이면 예외 발생")
    void createQuestionLike_MyQuestion_ThrowsException() {
        // given
        User user = createTestUser(1, "내질문", Role.USER);
        LikeRequest likeRequest = new LikeRequest(200);
        Question myQuestion = createTestQuestion(200, 1, 0);

        when(questionJpaRepository.findByIdAndState(200, ACTIVE))
                .thenReturn(Optional.of(myQuestion));

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionService.createQuestionLike(user, likeRequest));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(MY_QUESTION_LIKE.getMessage());
        verify(questionLikeJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("질문 좋아요 - 이미 좋아요한 질문 예외 발생")
    void createQuestionLike_AlreadyLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        LikeRequest likeRequest = new LikeRequest(300);
        Question question = createTestQuestion(300, 999, 10);

        when(questionJpaRepository.findByIdAndState(300, ACTIVE))
                .thenReturn(Optional.of(question));
        // 이미 좋아요 했다고 Mock
        when(questionLikeJpaRepository.existsByUserAndQuestion(user, question))
                .thenReturn(true);

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionService.createQuestionLike(user, likeRequest));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(QUESTION_ALREADY_LIKE.getMessage());
        verify(questionLikeJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("질문 좋아요 - 질문이 없음 예외 발생")
    void createQuestionLike_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        LikeRequest likeRequest = new LikeRequest(999);

        when(questionJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionService.createQuestionLike(user, likeRequest));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionLikeJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("질문 좋아요 취소 성공")
    void questionLikeCancel_Success() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        LikeRequest likeRequest = new LikeRequest(555);
        Question question = createTestQuestion(555, 999, 5);

        doNothing().when(idempotentProvider).isValidIdempotent(anyList());

        when(questionJpaRepository.findByIdAndState(555, ACTIVE))
                .thenReturn(Optional.of(question));
        // 이미 좋아요 했다고 가정
        when(questionLikeJpaRepository.existsByUserAndQuestion(user, question))
                .thenReturn(true);

        // when
        String result = questionService.questionLikeCancel(user, likeRequest);

        // then
        assertThat(result).isEqualTo("555번 프로젝트 좋아요 취소 완료");
        assertThat(question.getLikeCount()).isEqualTo(4); // 5 -> 4
        verify(questionLikeJpaRepository).deleteByUserAndQuestion(user, question);
    }

    @Test
    @DisplayName("질문 좋아요 취소 - 내 질문이면 예외 발생")
    void questionLikeCancel_MyQuestion_ThrowsException() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        Question myQuestion = createTestQuestion(777, 1, 10);
        LikeRequest likeRequest = new LikeRequest(777);

        when(questionJpaRepository.findByIdAndState(777, ACTIVE))
                .thenReturn(Optional.of(myQuestion));

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionService.questionLikeCancel(user, likeRequest));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(MY_QUESTION_LIKE.getMessage());
        verify(questionLikeJpaRepository, never()).deleteByUserAndQuestion(any(), any());
    }

    @Test
    @DisplayName("질문 좋아요 취소 - 좋아요한 적 없는 경우 예외 발생")
    void questionLikeCancel_NotLiked_ThrowsException() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        Question question = createTestQuestion(888, 999, 3);
        LikeRequest likeRequest = new LikeRequest(888);

        when(questionJpaRepository.findByIdAndState(888, ACTIVE))
                .thenReturn(Optional.of(question));
        // 좋아요하지 않은 상태
        when(questionLikeJpaRepository.existsByUserAndQuestion(user, question))
                .thenReturn(false);

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionService.questionLikeCancel(user, likeRequest));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_LIKE.getMessage());
        verify(questionLikeJpaRepository, never()).deleteByUserAndQuestion(any(), any());
    }

    @Test
    @DisplayName("질문 좋아요 취소 - 질문이 없음 예외 발생")
    void questionLikeCancel_QuestionNotFound_ThrowsException() {
        // given
        User user = createTestUser(1, "사용자", Role.USER);
        LikeRequest likeRequest = new LikeRequest(999);

        when(questionJpaRepository.findByIdAndState(999, ACTIVE))
                .thenReturn(Optional.empty());

        // when & then
        BaseException ex = assertThrows(BaseException.class,
                () -> questionService.questionLikeCancel(user, likeRequest));

        assertThat(ex.getErrorReason().getMessage()).isEqualTo(QUESTION_NOT_FOUND.getMessage());
        verify(questionLikeJpaRepository, never()).deleteByUserAndQuestion(any(), any());
    }


    private Question createTestQuestion(Integer questionId, Integer authorId, int likeCount) {
        return Question.builder()
                .id(questionId)
                .user(User.builder().id(authorId).build()) // question의 작성자
                .likeCount(likeCount)
                .build();
    }
    private User createTestUser(Integer userId, String name, Role role) {
        return User.builder()
                .id(userId)
                .name(name)
                .role(role)
                .build();
    }

    private Question createTestQuestion(Integer id, String title, String contents, User user) {
        Question question = Question.builder()
                .id(id)
                .title(title)
                .contents(contents)
                .user(user)
                .subjectName("알고리즘")
                .build();
        question.setQuestionFields(new ArrayList<>());
        return question;
    }

    private Semester createTestSemester(Integer id, String name) {
        return Semester.builder()
                .id(id)
                .name(name)
                .build();
    }

    private Field createTestField(Integer id, String name) {
        return Field.builder()
                .id(id)
                .name(name)
                .build();
    }

    private QuestionField createTestQuestionField(Integer id, Question question, Field field) {
        return QuestionField.builder()
                .id(new QuestionFieldId(question.getId(), field.getId()))
                .question(question)
                .field(field)
                .build();
    }

    private Category createTestCategory(Integer id, String name) {
        return Category.builder()
                .id(id)
                .name(name)
                .build();
    }


}