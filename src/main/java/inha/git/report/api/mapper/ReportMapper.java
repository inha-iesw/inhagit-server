package inha.git.report.api.mapper;

import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportReasonResponse;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.report.api.controller.dto.response.ReportTypeResponse;
import inha.git.report.domain.Report;
import inha.git.report.domain.ReportReason;
import inha.git.report.domain.ReportType;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * ReportMapper는 Report 엔티티와 관련된 데이터 변환 기능을 제공.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {

    /**
     * CreateReportRequest를 Report 엔티티로 변환
     *
     * @param user 사용자
     * @param createReportRequest 신고 생성 요청
     * @param reportType 신고 타입
     * @param reportReason 신고 원인
     * @return Report 엔티티
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reportedId", source = "createReportRequest.reportedId")
    @Mapping(target = "reporterId", source = "user.id")
    @Mapping(target = "reportedUserId", source = "reportedUser.id")
    @Mapping(target = "description", source = "createReportRequest.description")
    Report createReportRequestToReport(User user, User reportedUser, CreateReportRequest createReportRequest, ReportType reportType, ReportReason reportReason);

    /**
     * Report를 ReportResponse로 변환
     *
     * @param savedReport 신고
     * @return ReportResponse
     */
    @Mapping(target = "idx", source = "id")
    ReportResponse toReportResponse(Report savedReport);

    /**
     * ReportType을 ReportTypeResponse로 변환
     *
     * @param reportTypes 신고 타입
     * @return ReportTypeResponse
     */
    List<ReportTypeResponse> toReportTypeResponseList(List<ReportType> reportTypes);

    /**
     * ReportType을 ReportTypeResponse로 변환
     *
     * @param reportType 신고 타입
     * @return ReportTypeResponse
     */
    @Mapping(target = "idx", source = "id")
    ReportTypeResponse toReportTypeResponse(ReportType reportType);

    /**
     * ReportReason을 ReportReasonResponse로 변환
     *
     * @param reportReasons 신고 원인
     * @return ReportReasonResponse
     */
    List<ReportReasonResponse> toReportReasonResponseList(List<ReportReason> reportReasons);

    /**
     * ReportReason을 ReportReasonResponse로 변환
     *
     * @param reportReason 신고 원인
     * @return ReportReasonResponse
     */
    @Mapping(target = "idx", source = "id")
    ReportReasonResponse toReportReasonResponse(ReportReason reportReason);
}
