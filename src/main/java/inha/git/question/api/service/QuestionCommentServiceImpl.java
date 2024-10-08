package inha.git.question.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.mapping.domain.repository.QuestionCommentLikeJpaRepository;
import inha.git.mapping.domain.repository.QuestionReplyCommentLikeJpaRepository;
import inha.git.question.api.controller.dto.request.*;
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
import inha.git.utils.IdempotentProvider;
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
    private final QuestionCommentLikeJpaRepository questionCommentLikeJpaRepository;
    private final QuestionReplyCommentLikeJpaRepository questionReplyCommentLikeJpaRepository;
    private final QuestionMapper questionMapper;
    private final IdempotentProvider idempotentProvider;

    /**
     * 특정 질문 댓글 전체 조회
     *
     * @param questionIdx 질문 식별자
     * @return List<CommentWithRepliesResponse>
     */
    @Override
    public List<CommentWithRepliesResponse> getAllCommentsByQuestionIdx(User user, Integer questionIdx) {
        Question question = questionJpaRepository.findByIdAndState(questionIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        List<QuestionComment> comments = questionCommentJpaRepository.findAllByQuestionAndStateOrderByIdAsc(question, ACTIVE);
        return comments.stream()
                .map(comment -> {
                    // 댓글에 대한 likeState를 확인
                    boolean commentLikeState = questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, comment);
                    // 대댓글에 대한 likeState를 확인하여 변환
                    List<SearchReplyCommentResponse> replies = comment.getReplies().stream()
                            .filter(reply -> reply.getState().equals(ACTIVE))
                            .map(reply -> {
                                boolean replyLikeState = questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, reply);
                                return questionMapper.toSearchReplyCommentResponse(reply, replyLikeState);
                            })
                            .toList();
                    // 댓글과 대댓글 리스트를 포함하여 CommentWithRepliesResponse로 변환
                    return questionMapper.toCommentWithRepliesResponse(comment, commentLikeState, replies);
                })
                .toList();
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

        idempotentProvider.isValidIdempotent(List.of("createComment", user.getId().toString(), user.getName(), createCommentRequest.contents()));


        Question question = questionJpaRepository.findByIdAndState(createCommentRequest.questionIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
        QuestionComment questionComment = questionMapper.toQuestionComment(createCommentRequest, user, question);
        questionCommentJpaRepository.save(questionComment);

        question.increaseCommentCount();
        log.info("질문 댓글 생성 성공 - 사용자: {} 질문 ID: {}", user.getName(), createCommentRequest.questionIdx());
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
            log.error("질문 댓글 수정 권한 없음 - 사용자: {} 댓글 ID: {}", user.getName(), commentIdx);
            throw new BaseException(QUESTION_COMMENT_UPDATE_NOT_AUTHORIZED);
        }
        questionComment.setContents(updateCommentRequest.contents());
        questionCommentJpaRepository.save(questionComment);
        log.info("질문 댓글 수정 성공 - 사용자: {} 댓글 ID: {}", user.getName(), commentIdx);
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
            log.error("질문 댓글 삭제 권한 없음 - 사용자: {} 댓글 ID: {}", user.getName(), commentIdx);
            throw new BaseException(QUESTION_COMMENT_DELETE_NOT_AUTHORIZED);
        }

        if(questionComment.getDeletedAt() != null) {
            log.error("질문 댓글 삭제 실패 - 사용자: {} 이미 삭제된 댓글입니다.", user.getName());
            throw new BaseException(QUESTION_COMMENT_ALREADY_DELETED);
        }
        questionComment.setDeletedAt();
        if(questionReplyCommentJpaRepository.existsByQuestionCommentAndState(questionComment, ACTIVE)) {
            questionComment.setContents("삭제된 댓글입니다.");
        }
        else {
            questionComment.setState(INACTIVE);
        }
        questionCommentJpaRepository.save(questionComment);

        Question question = questionComment.getQuestion();
        question.decreaseCommentCount();

        log.info("질문 댓글 삭제 성공 - 사용자: {} 댓글 ID: {}", user.getName(), commentIdx);
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

        idempotentProvider.isValidIdempotent(List.of("createReplyComment", user.getId().toString(), user.getName(), createReplyCommentRequest.contents()));


        QuestionComment questionComment = questionCommentJpaRepository.findByIdAndState(createReplyCommentRequest.commentIdx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_NOT_FOUND));
        QuestionReplyComment questionReplyComment = questionMapper.toQuestionReplyComment(createReplyCommentRequest, user, questionComment);
        questionReplyCommentJpaRepository.save(questionReplyComment);

        Question question = questionComment.getQuestion();
        question.increaseCommentCount();

        log.info("질문 대댓글 생성 성공 - 사용자: {} 댓글 ID: {}", user.getName(), createReplyCommentRequest.commentIdx());
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
            log.error("질문 대댓글 수정 권한 없음 - 사용자: {} 댓글 ID: {}", user.getName(), replyCommentIdx);
            throw new BaseException(QUESTION_COMMENT_REPLY_UPDATE_NOT_AUTHORIZED);
        }
        questionReplyComment.setContents(updateCommentRequest.contents());
        questionReplyCommentJpaRepository.save(questionReplyComment);
        log.info("질문 대댓글 수정 성공 - 사용자: {} 댓글 ID: {}", user.getName(), replyCommentIdx);
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
            log.error("질문 대댓글 삭제 권한 없음 - 사용자: {} 댓글 ID: {}", user.getName(), replyCommentIdx);
            throw new BaseException(QUESTION_COMMENT_REPLY_DELETE_NOT_AUTHORIZED);
        }
        questionReplyComment.setState(INACTIVE);
        questionReplyComment.setDeletedAt();
        questionReplyCommentJpaRepository.save(questionReplyComment);

        Question question = questionReplyComment.getQuestionComment().getQuestion();
        question.decreaseCommentCount();

        log.info("질문 대댓글 삭제 성공 - 사용자: {} 댓글 ID: {}", user.getName(), replyCommentIdx);
        return questionMapper.toReplyCommentResponse(questionReplyComment);
    }

    /**
     * 질문 댓글 좋아요
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 정보
     * @return String
     */
    @Override
    public String questionCommentLike(User user, CommentLikeRequest commentLikeRequest) {

        idempotentProvider.isValidIdempotent(List.of("questionCommentLike", user.getId().toString(), user.getName(), commentLikeRequest.idx().toString()));


        QuestionComment questionComment = getQuestionComment(commentLikeRequest);
        validLike(questionComment, user, questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, questionComment));
        questionCommentLikeJpaRepository.save(questionMapper.createQuestionCommentLike(user, questionComment));
        questionComment.setLikeCount(questionComment.getLikeCount() + 1);
        log.info("질문 댓글 좋아요 성공 - 사용자: {} 댓글 ID: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), questionComment.getLikeCount());
        return commentLikeRequest.idx() + "번 질문 댓글 좋아요 완료";
    }

    /**
     * 질문 댓글 좋아요 취소
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 댓글 좋아요 정보
     * @return String
     */
    @Override
    public String questionCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest) {

        idempotentProvider.isValidIdempotent(List.of("questionCommentLikeCancel", user.getId().toString(), user.getName(), commentLikeRequest.idx().toString()));

        QuestionComment questionComment = getQuestionComment(commentLikeRequest);
        boolean commentLikeJpaRepository = questionCommentLikeJpaRepository.existsByUserAndQuestionComment(user, questionComment);
        validLikeCancel(questionComment, user, commentLikeJpaRepository);
        questionCommentLikeJpaRepository.deleteByUserAndQuestionComment(user, questionComment);
        questionComment.setLikeCount(questionComment.getLikeCount() - 1);
        log.info("질문 댓글 좋아요 취소 성공 - 사용자: {} 댓글 ID: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), questionComment.getLikeCount());
        return commentLikeRequest.idx() + "번 질문 댓글 좋아요 취소 완료";
    }

    /**
     * 질문 대댓글 좋아요
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 대댓글 좋아요 정보
     * @return String
     */
    @Override
    public String questionReplyCommentLike(User user, CommentLikeRequest commentLikeRequest) {

        idempotentProvider.isValidIdempotent(List.of("questionReplyCommentLike", user.getId().toString(), user.getName(), commentLikeRequest.idx().toString()));

        QuestionReplyComment questionReplyComment = questionReplyCommentJpaRepository.findByIdAndState(commentLikeRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_REPLY_NOT_FOUND));
        validReplyLike(questionReplyComment, user, questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, questionReplyComment));
        questionReplyCommentLikeJpaRepository.save(questionMapper.createQuestionReplyCommentLike(user, questionReplyComment));
        questionReplyComment.setLikeCount(questionReplyComment.getLikeCount() + 1);
        log.info("질문 대댓글 좋아요 성공 - 사용자: {} 댓글 ID: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), questionReplyComment.getLikeCount());
        return commentLikeRequest.idx() + "번 질문 대댓글 좋아요 완료";
    }

    /**
     * 질문 대댓글 좋아요 취소
     *
     * @param user 사용자 정보
     * @param commentLikeRequest 대댓글 좋아요 정보
     * @return String
     */
    @Override
    public String questionReplyCommentLikeCancel(User user, CommentLikeRequest commentLikeRequest) {

        idempotentProvider.isValidIdempotent(List.of("questionReplyCommentLikeCancel", user.getId().toString(), user.getName(), commentLikeRequest.idx().toString()));

        QuestionReplyComment questionReplyComment = questionReplyCommentJpaRepository.findByIdAndState(commentLikeRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_REPLY_NOT_FOUND));
        boolean commentLikeJpaRepository = questionReplyCommentLikeJpaRepository.existsByUserAndQuestionReplyComment(user, questionReplyComment);
        validReplyLikeCancel(questionReplyComment, user, commentLikeJpaRepository);
        questionReplyCommentLikeJpaRepository.deleteByUserAndQuestionReplyComment(user, questionReplyComment);
        questionReplyComment.setLikeCount(questionReplyComment.getLikeCount() - 1);
        log.info("질문 대댓글 좋아요 취소 성공 - 사용자: {} 댓글 ID: {} 좋아요 개수: {}", user.getName(), commentLikeRequest.idx(), questionReplyComment.getLikeCount());
        return commentLikeRequest.idx() + "번 질문 대댓글 좋아요 취소 완료";
    }


    /**
     * 댓글 좋아요 정보 유효성 검사
     *
     * @param questionComment 댓글 정보
     * @param user 사용자 정보
     * @param commentLikeJpaRepository 댓글 좋아요 레포지토리
     */
    private void validLike(QuestionComment questionComment, User user, boolean commentLikeJpaRepository) {
        if (questionComment.getUser().getId().equals(user.getId())) {
            log.error("내 댓글은 좋아요할 수 없습니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionComment.getId());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (commentLikeJpaRepository) {
            log.error("이미 좋아요한 댓글입니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionComment.getId());
            throw new BaseException(ALREADY_LIKE);
        }
    }

    /**
     * 대댓글 좋아요 정보 유효성 검사
     *
     * @param questionReplyComment 대댓글 정보
     * @param user 사용자 정보
     * @param commentLikeJpaRepository 대댓글 좋아요 레포지토리
     */
    private void validReplyLike(QuestionReplyComment questionReplyComment, User user, boolean commentLikeJpaRepository) {
        if (questionReplyComment.getUser().getId().equals(user.getId())) {
            log.error("내 대댓글은 좋아요할 수 없습니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionReplyComment.getId());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (commentLikeJpaRepository) {
            log.error("이미 좋아요한 대댓글입니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionReplyComment.getId());
            throw new BaseException(ALREADY_LIKE);
        }
    }

    /**
     * 댓글 좋아요 취소
     *
     * @param user 사용자 정보
     * @param questionComment 좋아요 취소할 댓글 정보
     * @param commentLikeJpaRepository 댓글 좋아요 레포지토리
     */
    private void validLikeCancel(QuestionComment questionComment, User user, boolean commentLikeJpaRepository) {
        if (questionComment.getUser().getId().equals(user.getId())) {
            log.error("내 댓글은 좋아요할 수 없습니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionComment.getId());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (!commentLikeJpaRepository) {
            log.error("좋아요하지 않은 댓글입니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionComment.getId());
            throw new BaseException(NOT_LIKE);
        }
    }

    /**
     * 대댓글 좋아요 취소
     *
     * @param user 사용자 정보
     * @param questionReplyComment 좋아요 취소할 대댓글 정보
     * @param commentLikeJpaRepository 대댓글 좋아요 레포지토리
     */
    private void validReplyLikeCancel(QuestionReplyComment questionReplyComment, User user, boolean commentLikeJpaRepository) {
        if (questionReplyComment.getUser().getId().equals(user.getId())) {
            log.error("내 대댓글은 좋아요할 수 없습니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionReplyComment.getId());
            throw new BaseException(MY_COMMENT_LIKE);
        }
        if (!commentLikeJpaRepository) {
            log.error("좋아요하지 않은 대댓글입니다. - 사용자: {} 댓글 ID: {}", user.getName(), questionReplyComment.getId());
            throw new BaseException(NOT_LIKE);
        }

    }
    /**
     * 댓글 좋아요 정보 조회
     *
     * @param commentLikeRequest 댓글 좋아요 정보
     * @return 댓글 좋아요 정보
     */
    private QuestionComment getQuestionComment(CommentLikeRequest commentLikeRequest) {
        return questionCommentJpaRepository.findByIdAndState(commentLikeRequest.idx(), ACTIVE)
                .orElseThrow(() -> new BaseException(QUESTION_COMMENT_NOT_FOUND));
    }
}

