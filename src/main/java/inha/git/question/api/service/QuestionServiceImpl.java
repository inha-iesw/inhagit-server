package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.repository.QuestionFieldJpaRepository;
import inha.git.mapping.domain.repository.QuestionLikeJpaRepository;
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
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.question.domain.repository.QuestionQueryRepository;
import inha.git.semester.controller.dto.response.SearchSemesterResponse;
import inha.git.semester.domain.Semester;
import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.semester.mapper.SemesterMapper;
import inha.git.statistics.api.service.StatisticsService;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * QuestionServiceImpl은 question 관련 비즈니스 로직을 처리.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionMapper questionMapper;
    private final SemesterMapper semesterMapper;
    private final QuestionFieldJpaRepository questionFieldJpaRepository;
    private final QuestionLikeJpaRepository questionLikeJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final SemesterJpaRepository semesterJpaRepository;
    private final QuestionQueryRepository questionQueryRepository;
    private final StatisticsService statisticsService;

    /**
     * 질문 전체 조회
     *
     * @param page Integer
     * @return Page<SearchQuestionsResponse>
     */
    @Override
    public Page<SearchQuestionsResponse> getQuestions(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return questionQueryRepository.getQuestions(pageable);
    }

    /**
     * 질문 조건 조회
     *
     * @param searchQuestionCond SearchQuestionCond
     * @param page               Integer
     * @return Page<SearchQuestionsResponse>
     */
    @Override
    public Page<SearchQuestionsResponse> getCondQuestions(SearchQuestionCond searchQuestionCond, Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return questionQueryRepository.getCondQuestions(searchQuestionCond, pageable);
    }

    /**
     * 질문 상세 조회
     *
     * @param questionIdx Integer
     * @return SearchQuestionResponse
     */
    @Override
    public SearchQuestionResponse getQuestion(User user, Integer questionIdx) {
        Question question = questionJpaRepository.findByIdAndState(questionIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        SearchSemesterResponse searchSemesterResponse = semesterMapper.semesterToSearchSemesterResponse(question.getSemester());
        SearchUserResponse searchUserResponse = questionMapper.userToSearchUserResponse(question.getUser());
        SearchLikeState likeState = questionMapper.questionToSearchLikeState(questionLikeJpaRepository.existsByUserAndQuestion(user, question));
        List<SearchFieldResponse> searchFieldResponses = questionFieldJpaRepository.findByQuestion(question)
                .stream()
                .map(questionField -> questionMapper.projectFieldToSearchFieldResponse(questionField.getField()))
                .toList();
        return questionMapper.questionToSearchQuestionResponse(question, searchFieldResponses, searchUserResponse, searchSemesterResponse, likeState);
    }


    /**
     * 질문 생성
     *
     * @param user                  User
     * @param createQuestionRequest CreateQuestionRequest
     * @return QuestionResponse
     */
    @Override
    @Transactional
    public QuestionResponse createQuestion(User user, CreateQuestionRequest createQuestionRequest) {
        Semester semester = semesterJpaRepository.findByIdAndState(createQuestionRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        Question question = questionMapper.createQuestionRequestToQuestion(createQuestionRequest, user, semester);
        Question saveQuestion = questionJpaRepository.save(question);

        List<QuestionField> questionFields = createAndSaveQuestionFields(createQuestionRequest.fieldIdxList(), saveQuestion);
        questionFieldJpaRepository.saveAll(questionFields);
        List<Field> fields = fieldJpaRepository.findAllById(createQuestionRequest.fieldIdxList());
        statisticsService.increaseCount(user, fields, semester, 2);
        log.info("질문 생성 성공 - 사용자: {} 질문 ID: {}", user.getName(), saveQuestion.getId());
        return questionMapper.questionToQuestionResponse(saveQuestion);
    }

    /**
     * 질문 수정
     *
     * @param user                User
     * @param questionIdx         Integer
     * @param updateQuestionRequest UpdateQuestionRequest
     * @return QuestionResponse
     */
    @Override
    @Transactional
    public QuestionResponse updateQuestion(User user, Integer questionIdx, UpdateQuestionRequest updateQuestionRequest) {
        Question question = questionJpaRepository.findByIdAndState(questionIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        if (!question.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            log.error("질문 수정 권한 없음 - 사용자: {} 질문 ID: {}", user.getName(), questionIdx);
            throw new BaseException(QUESTION_NOT_AUTHORIZED);
        }
        Semester semester = semesterJpaRepository.findByIdAndState(updateQuestionRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        questionMapper.updateQuestionRequestToQuestion(updateQuestionRequest, question, semester);
        Question savedQuestion = questionJpaRepository.save(question);
        questionFieldJpaRepository.deleteByQuestion(savedQuestion);

        List<QuestionField> questionFields = createAndSaveQuestionFields(updateQuestionRequest.fieldIdxList(), savedQuestion);
        questionFieldJpaRepository.saveAll(questionFields);
        log.info("질문 수정 성공 - 사용자: {} 질문 ID: {}", user.getName(), savedQuestion.getId());
        return questionMapper.questionToQuestionResponse(savedQuestion);
    }

    /**
     * 질문 삭제
     *
     * @param user        User
     * @param questionIdx Integer
     * @return QuestionResponse
     */
    @Override
    @Transactional
    public QuestionResponse deleteQuestion(User user, Integer questionIdx) {
        Question question = questionJpaRepository.findByIdAndState(questionIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        if (!question.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            log.error("질문 삭제 권한 없음 - 사용자: {} 질문 ID: {}", user.getName(), questionIdx);
            throw new BaseException(QUESTION_DELETE_NOT_AUTHORIZED);
        }
        question.setDeletedAt();
        question.setState(INACTIVE);
        questionJpaRepository.save(question);
        List<Field> fields = question.getQuestionFields().stream()
                .map(QuestionField::getField)
                .toList();
        statisticsService.decreaseCount(question.getUser(), fields, question.getSemester(), 2);
        log.info("질문 삭제 성공 - 사용자: {} 질문 ID: {}", user.getName(), questionIdx);
        return questionMapper.questionToQuestionResponse(question);
    }

    /**
     * 질문 좋아요 생성
     *
     * @param user        User
     * @param likeRequest LikeRequest
     * @return String
     */
    @Override
    @Transactional
    public String createQuestionLike(User user, LikeRequest likeRequest) {
        Question question = questionJpaRepository.findByIdAndState(likeRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        validLike(question, user, questionLikeJpaRepository.existsByUserAndQuestion(user, question));
        questionLikeJpaRepository.save(questionMapper.createQuestionLike(user, question));
        question.setLikeCount(question.getLikeCount() + 1);
        log.info("질문 좋아요 성공 - 사용자: {} 질문 ID: {} 좋아요 개수 : {}", user.getName(), likeRequest.idx(), question.getLikeCount());
        return likeRequest.idx() + "번 질문 좋아요 완료";
    }

    /**
     * 질문 좋아요 취소
     *
     * @param user        User
     * @param likeRequest LikeRequest
     * @return String
     */
    @Override
    @Transactional
    public String questionLikeCancel(User user, LikeRequest likeRequest) {
        Question question = questionJpaRepository.findByIdAndState(likeRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        validLikeCancel(question, user, questionLikeJpaRepository.existsByUserAndQuestion(user, question));
        questionLikeJpaRepository.deleteByUserAndQuestion(user, question);
        question.setLikeCount(question.getLikeCount() - 1);
        log.info("질문 좋아요 취소 성공 - 사용자: {} 질문 ID: {} 좋아요 개수 : {}", user.getName(), likeRequest.idx(), question.getLikeCount());
        return likeRequest.idx() + "번 프로젝트 좋아요 취소 완료";
    }


    /**
     * 질문 생성시 필드 생성
     *
     * @param fieldIdxList List<Integer>
     * @param question     Question
     * @return List<QuestionField>
     */
    private List<QuestionField> createAndSaveQuestionFields(List<Integer> fieldIdxList, Question question) {
        return fieldIdxList.stream()
                .map(fieldIdx -> {
                    Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    return questionMapper.createQuestionField(question, field);
                }).toList();
    }

    /**
     * 좋아요 유효성 검사
     *
     * @param question Question
     * @param user     User
     * @param questionLikeJpaRepository 질문 좋아요 레포지토리
     */
    private void validLike(Question question, User user, boolean questionLikeJpaRepository) {
        if (question.getUser().getId().equals(user.getId())) {
            log.error("내 질문은 좋아요할 수 없습니다. - 사용자: {} 질문 ID: {}", user.getName(), question.getId());
            throw new BaseException(MY_QUESTION_LIKE);
        }
        if (questionLikeJpaRepository) {
            log.error("이미 좋아요한 질문입니다. - 사용자: {} 질문 ID: {}", user.getName(), question.getId());
            throw new BaseException(QUESTION_ALREADY_LIKE);
        }
    }

    private void validLikeCancel(Question question, User user, boolean questionLikeJpaRepository) {
        if (question.getUser().getId().equals(user.getId())) {
            log.error("내 질문은 좋아요할 수 없습니다. - 사용자: {} 질문 ID: {}", user.getName(), question.getId());
            throw new BaseException(MY_QUESTION_LIKE);
        }
        if (!questionLikeJpaRepository) {
            log.error("좋아요하지 않은 질문입니다. - 사용자: {} 질문 ID: {}", user.getName(), question.getId());
            throw new BaseException(QUESTION_NOT_LIKE);
        }
    }
}
