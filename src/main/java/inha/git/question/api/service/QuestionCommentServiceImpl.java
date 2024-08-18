package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.question.api.controller.dto.request.CommentWithRepliesResponse;
import inha.git.question.api.controller.dto.request.CreateCommentRequest;
import inha.git.question.api.controller.dto.request.CreateReplyCommentRequest;
import inha.git.question.api.controller.dto.request.UpdateCommentRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionCommentServiceImpl implements QuestionCommentService {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionCommentJpaRepository questionCommentJpaRepository;
    private final QuestionReplyCommentJpaRepository questionReplyCommentJpaRepository;
    private final QuestionMapper questionMapper;

    /**
     * 특정 질문 댓글 전체 조회
     *
     * @param questionIdx 질문 식별자
     * @return List<CommentWithRepliesResponse>
     */
    @Override
    public List<CommentWithRepliesResponse> getAllCommentsByQuestionIdx(Integer questionIdx) {
        Question question = questionJpaRepository.findByIdAndState(questionIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        List<QuestionComment> comments = questionCommentJpaRepository.findAllByQuestionAndStateOrderByIdAsc(question, ACTIVE);
        return questionMapper.toCommentWithRepliesResponseList(comments);
    }
    /**
     * 댓글 생성
     *
     * @param user                사용자 정보
     * @param createCommentRequest 댓글 생성 요청
     * @return CreateCommentResponse
     */
    @Override
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

    /**
     * 답글 생성
     *
     * @param user                        사용자 정보
     * @param createReplyCommentRequest 댓글 생성 요청
     * @return ReplyCommentResponse
     */
    @Override
    public ReplyCommentResponse createReplyComment(User user, CreateReplyCommentRequest createReplyCommentRequest) {
        QuestionComment questionComment = questionCommentJpaRepository.findByIdAndState(createReplyCommentRequest.commentIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_NOT_FOUND));
        QuestionReplyComment questionReplyComment = questionMapper.toQuestionReplyComment(createReplyCommentRequest, user, questionComment);
        questionReplyCommentJpaRepository.save(questionReplyComment);
        return questionMapper.toReplyCommentResponse(questionReplyComment);
    }

    /**
     * 답글 수정
     *
     * @param user                사용자 정보
     * @param replyCommentIdx          댓글 식별자
     * @param updateCommentRequest 댓글 수정 요청
     * @return ReplyCommentResponse
     */
    @Override
    public ReplyCommentResponse updateReplyComment(User user, Integer replyCommentIdx, UpdateCommentRequest updateCommentRequest) {
        QuestionReplyComment questionReplyComment = questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_REPLY_NOT_FOUND));
        if(!questionReplyComment.getUser().getId().equals(user.getId())) {
            throw new BaseException(QUESTION_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED);
        }
        questionReplyComment.setContents(updateCommentRequest.contents());
        questionReplyCommentJpaRepository.save(questionReplyComment);
        return questionMapper.toReplyCommentResponse(questionReplyComment);
    }

    /**
     * 답글 삭제
     *
     * @param user                사용자 정보
     * @param replyCommentIdx          댓글 식별자
     * @return ReplyCommentResponse
     */
    @Override
    public ReplyCommentResponse deleteReplyComment(User user, Integer replyCommentIdx) {
        QuestionReplyComment questionReplyComment = questionReplyCommentJpaRepository.findByIdAndState(replyCommentIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_REPLY_NOT_FOUND));
        if(!questionReplyComment.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new BaseException(QUESTION_COMMENT_REPLY_DELETE_NOT_AUTHORIZED);
        }
        questionReplyComment.setState(INACTIVE);
        questionReplyComment.setDeletedAt();
        questionReplyCommentJpaRepository.save(questionReplyComment);
        return questionMapper.toReplyCommentResponse(questionReplyComment);
    }


}
