package inha.git.report.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.project.domain.Project;
import inha.git.project.domain.ProjectComment;
import inha.git.project.domain.ProjectReplyComment;
import inha.git.project.domain.repository.ProjectCommentJpaRepository;
import inha.git.project.domain.repository.ProjectJpaRepository;
import inha.git.project.domain.repository.ProjectReplyCommentJpaRepository;
import inha.git.question.domain.Question;
import inha.git.question.domain.QuestionComment;
import inha.git.question.domain.QuestionReplyComment;
import inha.git.question.domain.repository.QuestionCommentJpaRepository;
import inha.git.question.domain.repository.QuestionJpaRepository;
import inha.git.question.domain.repository.QuestionReplyCommentJpaRepository;
import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportReasonResponse;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.report.api.controller.dto.response.ReportTypeResponse;
import inha.git.report.api.mapper.ReportMapper;
import inha.git.report.domain.Report;
import inha.git.report.domain.ReportReason;
import inha.git.report.domain.ReportType;
import inha.git.report.domain.repository.ReportJpaRepository;
import inha.git.report.domain.repository.ReportReasonJpaRepository;
import inha.git.report.domain.repository.ReportTypeJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.repository.UserJpaRepository;
import inha.git.utils.IdempotentProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.*;
import static inha.git.user.domain.enums.Role.ADMIN;

/**
 * ReportServiceImpl은 ReportService 인터페이스를 구현.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportServiceImpl implements ReportService{

    private final ReportJpaRepository reportJpaRepository;
    private final ReportReasonJpaRepository reportReasonJpaRepository;
    private final ReportTypeJpaRepository reportTypeJpaRepository;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectCommentJpaRepository projectCommentJpaRepository;
    private final ProjectReplyCommentJpaRepository projectReplyCommentJpaRepository;
    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionCommentJpaRepository questionCommentJpaRepository;
    private final QuestionReplyCommentJpaRepository questionReplyCommentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ReportMapper reportMapper;
    private final IdempotentProvider idempotentProvider;

    /**
     * 신고 타입 조회
     *
     * @return List<ReportTypeResponse>
     */
    @Override
    public List<ReportTypeResponse> getReportTypes() {
        return reportMapper.toReportTypeResponseList(reportTypeJpaRepository.findAll());
    }

    /**
     * 신고 원인 조회
     *
     * @return List<ReportReasonResponse>
     */
    @Override
    public List<ReportReasonResponse> getReportReasons() {
        return reportMapper.toReportReasonResponseList(reportReasonJpaRepository.findAll());
    }

    /**
     * 신고 생성
     *
     * @param user 사용자
     * @param createReportRequest 신고 생성 요청
     * @return ReportResponse
     */
    @Override
    public ReportResponse createReport(User user, CreateReportRequest createReportRequest) {
        idempotentProvider.isValidIdempotent(List.of("createReportRequest", user.getId().toString(), user.getName(), createReportRequest.reportedId().toString(), createReportRequest.reportTypeId().toString(), createReportRequest.reportReasonId().toString()));

        ReportType reportType = reportTypeJpaRepository.findById(createReportRequest.reportTypeId())
                .orElseThrow(() -> new BaseException(REPORT_TYPE_NOT_FOUND));
        User reportedUser = validateReportType(user.getId(), createReportRequest.reportedId(), reportType);
        reportedUser.increaseReportCount();
        userJpaRepository.save(reportedUser);
        ReportReason reportReason = reportReasonJpaRepository.findById(createReportRequest.reportReasonId())
                .orElseThrow(() -> new BaseException(REPORT_REASON_NOT_FOUND));
        if (reportJpaRepository.existsByReporterIdAndReportedUserIdAndReportedIdAndReportTypeAndState(
                user.getId(), reportedUser.getId(), createReportRequest.reportedId(), reportType, ACTIVE)) {
            throw new BaseException(DUPLICATE_REPORT);
        }
        Report report = reportMapper.createReportRequestToReport(user, reportedUser,  createReportRequest, reportType, reportReason);
        Report savedReport = reportJpaRepository.save(report);
        return reportMapper.toReportResponse(savedReport);
    }

    /**
     * 신고 삭제
     *
     * @param user 사용자
     * @param reportId 신고 ID
     * @return ReportResponse
     */
    @Override
    public ReportResponse deleteReport(User user, Integer reportId) {

        idempotentProvider.isValidIdempotent(List.of("deleteReport", user.getId().toString(), user.getName(), reportId.toString()));

        Report report = reportJpaRepository.findByIdAndState(reportId, ACTIVE)
                .orElseThrow(() -> new BaseException(REPORT_NOT_FOUND));
        if (!user.getRole().equals(ADMIN) && !report.getReporterId().equals(user.getId())) {
            throw new BaseException(CANNOT_DELETE_REPORT);
        }
        User reportedUser = userJpaRepository.findById(report.getReportedUserId())
                .orElseThrow(() -> new BaseException(NOT_FIND_USER));
        reportedUser.decreaseReportCount();
        userJpaRepository.save(reportedUser);
        report.setDeletedAt();
        report.setState(INACTIVE);
        Report savedReport = reportJpaRepository.save(report);
        return reportMapper.toReportResponse(savedReport);
    }

    private User validateReportType(Integer userId, Integer reportedId, ReportType reportType) {
        log.info("reportType: {}", reportType.getName());
        switch (reportType.getName()) {
            case "I-FOSS":
                Project project = projectJpaRepository.findById(reportedId)
                        .orElseThrow(() -> new BaseException(PROJECT_NOT_FOUND));
                if(project.getUser().getId().equals(userId))
                    throw new BaseException(CANNOT_REPORT_MYSELF);
                return project.getUser();
            case "ISSS":
                Question question = questionJpaRepository.findById(reportedId)
                        .orElseThrow(() -> new BaseException(QUESTION_NOT_FOUND));
                if(question.getUser().getId().equals(userId)) {
                    throw new BaseException(CANNOT_REPORT_MYSELF);
                }
                return question.getUser();
            case "I-FOSS-COMMENT":
                ProjectComment projectComment = projectCommentJpaRepository.findById(reportedId)
                        .orElseThrow(() -> new BaseException(PROJECT_COMMENT_NOT_FOUND));
                if(projectComment.getUser().getId().equals(userId)) {
                    throw new BaseException(CANNOT_REPORT_MYSELF);
                }
                return projectComment.getUser();
            case "ISSS-COMMENT":
                QuestionComment questionComment = questionCommentJpaRepository.findById(reportedId)
                        .orElseThrow(() -> new BaseException(QUESTION_COMMENT_NOT_FOUND));
                if(questionComment.getUser().getId().equals(userId)) {
                    throw new BaseException(CANNOT_REPORT_MYSELF);
                }
                return questionComment.getUser();
            case "I-FOSS-REPLY":
                ProjectReplyComment projectReplyComment = projectReplyCommentJpaRepository.findById(reportedId)
                        .orElseThrow(() -> new BaseException(PROJECT_COMMENT_REPLY_NOT_FOUND));
                if(projectReplyComment.getUser().getId().equals(userId)) {
                    throw new BaseException(CANNOT_REPORT_MYSELF);
                }
                return projectReplyComment.getUser();
            case "ISSS-REPLY":
                QuestionReplyComment questionReplyComment = questionReplyCommentJpaRepository.findById(reportedId)
                        .orElseThrow(() -> new BaseException(QUESTION_COMMENT_REPLY_NOT_FOUND));
                if(questionReplyComment.getUser().getId().equals(userId)) {
                    throw new BaseException(CANNOT_REPORT_MYSELF);
                }
                return questionReplyComment.getUser();
            default:
                throw new BaseException(REPORT_TYPE_NOT_FOUND);
        }
    }
}
