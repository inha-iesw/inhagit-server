package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.question.api.controller.dto.request.CreateCommentRequest;
import inha.git.question.api.controller.dto.request.UpdateCommentRequest;
import inha.git.question.api.controller.dto.response.CommentResponse;
import inha.git.question.api.mapper.QuestionMapper;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.repository.QuestionCommentJpaRepository;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

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

    /**
     * 댓글 수정
     *
     * @param user                사용자 정보
     * @param commentIdx           댓글 식별자
     * @param updateCommentRequest 댓글 수정 요청
     * @return UpdateCommentResponse
     */

    @Override
    @Transactional
    public CommentResponse updateComment(User user, Integer commentIdx, UpdateCommentRequest updateCommentRequest) {
        QuestionComment questionComment = questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_NOT_FOUND));
        if(!questionComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(QUESTION_COMMENT_UPDATE_NOT_AUTHORIZED);
        }
        questionComment.setContents(updateCommentRequest.contents());
        questionCommentJpaRepository.save(questionComment);
        return questionMapper.toCommentResponse(questionComment);
    }

    /**
     * 댓글 삭제
     *
     * @param user        사용자 정보
     * @param commentIdx  댓글 식별자
     * @return DeleteCommentResponse
     */
    @Override
    @Transactional
    public CommentResponse deleteComment(User user, Integer commentIdx) {
        QuestionComment questionComment = questionCommentJpaRepository.findByIdAndState(commentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_NOT_FOUND));
        if(!questionComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(QUESTION_COMMENT_DELETE_NOT_AUTHORIZED);
        }
        questionComment.setState(INACTIVE);
        questionComment.setDeletedAt();
        questionCommentJpaRepository.save(questionComment);
        return questionMapper.toCommentResponse(questionComment);
    }
}
