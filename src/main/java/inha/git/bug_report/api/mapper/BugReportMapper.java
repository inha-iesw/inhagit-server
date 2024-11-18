package inha.git.bug_report.api.mapper;

import inha.git.bug_report.api.controller.dto.request.CreateBugReportRequest;
import inha.git.bug_report.api.controller.dto.request.UpdateBugReportRequest;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.bug_report.domain.BugReport;
import inha.git.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


/**
 * BugReportMapper는 버그 제보를 변환하는 인터페이스.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BugReportMapper {


    /**
     * createBugReportRequestToBugReport는 CreateBugReportRequest를 BugReport로 변환하는 메소드.
     * @param user
     * @param createBugReportRequest
     * @return BugReport
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bugStatus", constant = "UNCONFIRMED")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "createBugReportRequest.title", target = "title")
    @Mapping(source = "createBugReportRequest.contents", target = "contents")
    BugReport createBugReportRequestToBugReport(User user, CreateBugReportRequest createBugReportRequest);

    /**
     * bugReportToBugReportResponse는 BugReport를 BugReportResponse로 변환하는 메소드.
     * @param bugReport
     * @return BugReportResponse
     */
    @Mapping(target = "idx", source = "id")
    BugReportResponse bugReportToBugReportResponse(BugReport bugReport);


    /**
     * updateBugReportRequestToBugReport는 UpdateBugReportRequest를 BugReport로 변환하는 메소드.
     * @param bugReport
     * @param updateBugReportRequest
     */
    @Mapping(target = "title", source = "updateBugReportRequest.title")
    @Mapping(target = "contents", source = "updateBugReportRequest.contents")
    void updateBugReportRequestToBugReport(@MappingTarget BugReport bugReport, UpdateBugReportRequest updateBugReportRequest);

}
