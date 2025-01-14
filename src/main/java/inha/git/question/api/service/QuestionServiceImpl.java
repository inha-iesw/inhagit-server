package inha.git.question.api.service;

import inha.git.category.domain.Category;
import inha.git.category.domain.repository.CategoryJpaRepository;
import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.id.QuestionFieldId;
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
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.Constant.CREATE_AT;
import static inha.git.common.Constant.CURRICULUM;
import static inha.git.common.code.status.ErrorStatus.*;

/**
 * 질문 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * 질문의 조회, 생성, 수정, 삭제 및 관련 통계 처리를 담당합니다.
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
    private final CategoryJpaRepository categoryJpaRepository;
    private final QuestionQueryRepository questionQueryRepository;
    private final IdempotentProvider idempotentProvider;
    private final StatisticsService statisticsService;

    /**
     * 전체 질문을 페이징하여 조회합니다.
     *
     * @param page 조회할 페이지 번호 (0부터 시작)
     * @param size 페이지당 항목 수
     * @return 페이징된 질문 목록
     */
    @Override
    public Page<SearchQuestionsResponse> getQuestions(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return questionQueryRepository.getQuestions(pageable);
    }

    /**
     * 질문 조건 조회
     *
     * @param searchQuestionCond SearchQuestionCond
     * @param page               Integer
     * @param size               Integer
     * @return Page<SearchQuestionsResponse>
     */
    @Override
    public Page<SearchQuestionsResponse> getCondQuestions(SearchQuestionCond searchQuestionCond, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, CREATE_AT));
        return questionQueryRepository.getCondQuestions(searchQuestionCond, pageable);
    }

    /**
     * 질문 상세 조회
     *
     * @param questionIdx Integer
     * @return SearchQuestionResponse
     * @throws BaseException QUESTION_NOT_FOUND: 질문을 찾을 수 없는 경우
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
     * @throws BaseException SEMESTER_NOT_FOUND: 학기를 찾을 수 없는 경우
     *                     CATEGORY_NOT_FOUND: 카테고리를 찾을 수 없는 경우
     *                     FIELD_NOT_FOUND: 필드를 찾을 수 없는 경우
     *                     QUESTION_NOT_AUTHORIZED: 질문 수정 권한이 없는 경우
     */
    @Override
    @Transactional
    public QuestionResponse createQuestion(User user, CreateQuestionRequest createQuestionRequest) {

        idempotentProvider.isValidIdempotent(List.of("createQuestion", user.getName(), user.getId().toString(), createQuestionRequest.title(), createQuestionRequest.contents(), createQuestionRequest.subject()));


        Semester semester = semesterJpaRepository.findByIdAndState(createQuestionRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));
        Category category = categoryJpaRepository.findByNameAndState(CURRICULUM, ACTIVE)
                    .orElseThrow(() -> new BaseException(CATEGORY_NOT_FOUND));

        Question question = questionMapper.createQuestionRequestToQuestion(createQuestionRequest, user, semester, category);
        Question saveQuestion = questionJpaRepository.save(question);

        List<QuestionField> questionFields = createAndSaveQuestionFields(createQuestionRequest.fieldIdxList(), saveQuestion);
        questionFieldJpaRepository.saveAll(questionFields);
        List<Field> fields = fieldJpaRepository.findAllById(createQuestionRequest.fieldIdxList());
        statisticsService.adjustCount(user, fields, semester, category, 3, true);
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
     * @throws BaseException SEMESTER_NOT_FOUND: 학기를 찾을 수 없는 경우
     *                    FIELD_NOT_FOUND: 필드를 찾을 수 없는 경우
     *                    QUESTION_NOT_AUTHORIZED: 질문 수정 권한이 없는 경우
     *                    FIELD_NOT_FOUND: 필드를 찾을 수 없는 경우
     *                    QUESTION_NOT_FOUND: 질문을 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public QuestionResponse updateQuestion(User user, Integer questionIdx, UpdateQuestionRequest updateQuestionRequest) {
        idempotentProvider.isValidIdempotent(List.of("updateQuestion", user.getName(), user.getId().toString(), updateQuestionRequest.title(), updateQuestionRequest.contents(), updateQuestionRequest.subject()));

        Question question = questionJpaRepository.findByIdAndState(questionIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));

        if (!question.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            log.error("질문 수정 권한 없음 - 사용자: {} 질문 ID: {}", user.getName(), questionIdx);
            throw new BaseException(QUESTION_NOT_AUTHORIZED);
        }

        // 변경 전 상태 저장
        Semester originSemester = question.getSemester();
        Category originCategory = question.getCategory();

        List<Field> originFields = question.getQuestionFields().stream()
                .map(QuestionField::getField)
                .toList();

        // 새로운 학기 정보 가져오기
        Semester newSemester = semesterJpaRepository.findByIdAndState(updateQuestionRequest.semesterIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(SEMESTER_NOT_FOUND));

        // 새로운 필드 정보 처리
        List<Integer> newFieldIds = updateQuestionRequest.fieldIdxList();
        List<Field> newFields = fieldJpaRepository.findAllById(newFieldIds);

        // 질문 정보 업데이트
        questionMapper.updateQuestionRequestToQuestion(updateQuestionRequest, question, newSemester, originCategory);

        // 필드 정보 업데이트 (최적화된 로직)
        Set<Integer> existingFieldIds = question.getQuestionFields().stream()
                .map(qf -> qf.getField().getId())
                .collect(Collectors.toSet());

        Set<Integer> newFieldIdSet = new HashSet<>(newFieldIds);


        // 삭제해야 할 분야 처리
        existingFieldIds.stream()
                .filter(id -> !newFieldIdSet.contains(id))
                .forEach(id -> {
                    QuestionField questionField = question.getQuestionFields().stream()
                            .filter(qf -> qf.getField().getId().equals(id))
                            .findFirst()
                            .orElse(null);
                    if (questionField != null) {
                        question.getQuestionFields().remove(questionField);
                        questionFieldJpaRepository.delete(questionField);
                    }
                });

        // 새로 추가해야 할 분야 처리
        newFieldIdSet.stream()
                .filter(id -> !existingFieldIds.contains(id))
                .forEach(id -> {
                    Field field = fieldJpaRepository.findById(id)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    QuestionField newQuestionField = new QuestionField(new QuestionFieldId(questionIdx, id), question, field);
                    question.getQuestionFields().add(newQuestionField);
                    questionFieldJpaRepository.save(newQuestionField);
                });

        Question savedQuestion = questionJpaRepository.save(question);

        // 통계 업데이트
        // 이전 상태에 대한 통계 감소
        statisticsService.adjustCount(user, originFields, originSemester, originCategory, 3, false);
        // 새로운 상태에 대한 통계 증가
        statisticsService.adjustCount(user, newFields, newSemester, originCategory, 3, true);

        log.info("질문 수정 성공 - 사용자: {} 질문 ID: {}", user.getName(), savedQuestion.getId());
        return questionMapper.questionToQuestionResponse(savedQuestion);
    }
    /**
     * 질문 삭제
     *
     * @param user        User
     * @param questionIdx Integer
     * @return QuestionResponse
     * @throws BaseException QUESTION_DELETE_NOT_AUTHORIZED: 질문 삭제 권한이 없는 경우
     *                    QUESTION_NOT_FOUND: 질문을 찾을 수 없는 경우
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
        statisticsService.adjustCount(question.getUser(), fields, question.getSemester(), question.getCategory(), 3, false);
        log.info("질문 삭제 성공 - 사용자: {} 질문 ID: {}", user.getName(), questionIdx);
        return questionMapper.questionToQuestionResponse(question);
    }

    /**
     * 질문 좋아요 생성
     *
     * @param user        User
     * @param likeRequest LikeRequest
     * @return String
     * @throws BaseException QUESTION_NOT_FOUND: 질문을 찾을 수 없는 경우
     *                   MY_QUESTION_LIKE: 내 질문은 좋아요할 수 없는 경우
     *                   QUESTION_ALREADY_LIKE: 이미 좋아요한 질문인 경우
     */
    @Override
    @Transactional
    public String createQuestionLike(User user, LikeRequest likeRequest) {
        Question question = getQuestion(user, likeRequest);
        try{
            validLike(question, user, questionLikeJpaRepository.existsByUserAndQuestion(user, question));
            questionLikeJpaRepository.save(questionMapper.createQuestionLike(user, question));
            question.setLikeCount(question.getLikeCount() + 1);
            log.info("질문 좋아요 성공 - 사용자: {} 질문 ID: {} 좋아요 개수 : {}", user.getName(), likeRequest.idx(), question.getLikeCount());
            return likeRequest.idx() + "번 질문 좋아요 완료";
        } catch(DataIntegrityViolationException e) {
            log.error("질문 좋아요 중복 발생 - 사용자: {} 댓글 ID: {}", user.getName(), likeRequest.idx());
            throw new BaseException(ALREADY_LIKE);
        }
    }

    /**
     * 질문 좋아요 취소
     *
     * @param user        User
     * @param likeRequest LikeRequest
     * @return String
     * @throws BaseException QUESTION_NOT_FOUND: 질문을 찾을 수 없는 경우
     *                    MY_QUESTION_LIKE: 내 질문은 좋아요할 수 없는 경우
     *                    QUESTION_NOT_LIKE: 좋아요하지 않은 질문인 경우
     *
     */
    @Override
    @Transactional
    public String questionLikeCancel(User user, LikeRequest likeRequest) {
        Question question = getQuestion(user, likeRequest);
        try{
            validLikeCancel(question, user, questionLikeJpaRepository.existsByUserAndQuestion(user, question));
            questionLikeJpaRepository.deleteByUserAndQuestion(user, question);
            question.setLikeCount(question.getLikeCount() - 1);
            log.info("질문 좋아요 취소 성공 - 사용자: {} 질문 ID: {} 좋아요 개수 : {}", user.getName(), likeRequest.idx(), question.getLikeCount());
            return likeRequest.idx() + "번 프로젝트 좋아요 취소 완료";
        } catch(DataIntegrityViolationException e) {
            log.error("질문 좋아요 취소 중복 발생 - 사용자: {} 댓글 ID: {}", user.getName(), likeRequest.idx());
            throw new BaseException(QUESTION_NOT_LIKE);
        }
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

    private Question getQuestion(User user, LikeRequest likeRequest) {
        Question question;
        try {
            question = questionJpaRepository.findByIdAndStateWithPessimisticLock(likeRequest.idx(), ACTIVE)
                    .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        } catch (PessimisticLockingFailureException e) {
            log.error("질문 좋아요 추천 락 획득 실패- 사용자: {} 댓글 ID: {}", user.getName(), likeRequest.idx());
            throw new BaseException(TEMPORARY_UNAVAILABLE);
        }
        return question;
    }
}
