package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.mapping.domain.QuestionField;
import inha.git.mapping.domain.repository.QuestionFieldJpaRepository;
import inha.git.question.api.controller.dto.request.CreateQuestionRequest;
import inha.git.question.api.controller.dto.response.QuestionResponse;
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionMapper questionMapper;
    private final QuestionFieldJpaRepository questionFieldJpaRepository;
    private final FieldJpaRepository fieldJpaRepository;

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

        List<QuestionField> questionFields = createAndSaveProjectFields(createQuestionRequest.fieldIdxList(), saveQuestion);
        questionFieldJpaRepository.saveAll(questionFields);
        return questionMapper.questionToQuestionResponse(saveQuestion);
    }


    /**
     * 질문 생성시 필드 생성
     *
     * @param fieldIdxList List<Integer>
     * @param question     Question
     * @return List<QuestionField>
     */
    private List<QuestionField> createAndSaveProjectFields(List<Integer> fieldIdxList, Question question) {
        return fieldIdxList.stream()
                .map(fieldIdx -> {
                    Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                            .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
                    return questionMapper.createQuestionField(question, field);
                }).toList();
    }
}
