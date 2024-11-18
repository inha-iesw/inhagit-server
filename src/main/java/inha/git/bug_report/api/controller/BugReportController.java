package inha.git.bug_report.api.controller;

import inha.git.bug_report.api.service.BugReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * BugReportController는 버그 제보 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "bug-report controller", description = "bug-report 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/bug-reports")
public class BugReportController {

    private final BugReportService bannerService;

}
