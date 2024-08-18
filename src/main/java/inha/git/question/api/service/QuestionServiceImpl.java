package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.repository.QuestionFieldJpaRepository;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.request.SearchQuestionsResponse;
import inha.git.question.api.controller.dto.request.UpdateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.question.domain.repository.QuestionQueryRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionMapper questionMapper;
    private final QuestionFieldJpaRepository questionFieldJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;
    private final QuestionQueryRepository questionQueryRepository;

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
     * 질문 생성
     *
     * @param user                  User
     * @param createQuestionRequest CreateQuestionRequest
     * @return QuestionResponse
     */
    @Override
    @Transactional
    public QuestionResponse createQuestion(User user, CreateQuestionRequest createQuestionRequest) {
        Question question = questionMapper.createQuestionRequestToQuestion(createQuestionRequest, user);
        Question saveQuestion = questionJpaRepository.save(question);

        List<QuestionField> questionFields = createAndSaveQuestionFields(createQuestionRequest.fieldIdxList(), saveQuestion);
        questionFieldJpaRepository.saveAll(questionFields);
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
            throw new BaseException(QUESTION_NOT_AUTHORIZED);
        }
        questionMapper.updateQuestionRequestToQuestion(updateQuestionRequest, question);
        Question savedQuestion = questionJpaRepository.save(question);
        questionFieldJpaRepository.deleteByQuestion(savedQuestion);

        List<QuestionField> questionFields = createAndSaveQuestionFields(updateQuestionRequest.fieldIdxList(), savedQuestion);
        questionFieldJpaRepository.saveAll(questionFields);
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
            throw new BaseException(QUESTION_DELETE_NOT_AUTHORIZED);
        }
        question.setDeletedAt();
        question.setState(INACTIVE);
        questionJpaRepository.save(question);
        return questionMapper.questionToQuestionResponse(question);
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
}
