package inha.git.report.api.mapper;

import inha.git.report.api.controller.dto.request.CreateReportRequest;
import inha.git.report.api.controller.dto.response.ReportResponse;
import inha.git.report.domain.Report;
import inha.git.report.domain.ReportReason;
import inha.git.report.domain.ReportType;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

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
    @Mapping(target = "description", source = "createReportRequest.description")
    Report createReportRequestToReport(User user, CreateReportRequest createReportRequest, ReportType reportType, ReportReason reportReason);

    /**
     * Report를 ReportResponse로 변환
     *
     * @param savedReport 신고
     * @return ReportResponse
     */
    @Mapping(target = "idx", source = "id")
    ReportResponse toReportResponse(Report savedReport);
}
