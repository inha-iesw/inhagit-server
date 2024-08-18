package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.request.CreateCommentRequest;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.repository.QuestionCommentJpaRepository;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.code.status.ErrorStatus.QUESTION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionCommentJpaRepository questionCommentJpaRepository;
    private final QuestionMapper questionMapper;

    /**
     * 댓글 생성
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return CreateCommentResponse
     */
    @Override
    @Transactional
    public CommentResponse createComment(User user, CreateCommentRequest createCommentRequest) {
        Question question = questionJpaRepository.findByIdAndState(createCommentRequest.questionIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        QuestionComment questionComment = questionMapper.toQuestionComment(createCommentRequest, user, question);
        questionCommentJpaRepository.save(questionComment);
        return questionMapper.toCommentResponse(questionComment);
    }
}
