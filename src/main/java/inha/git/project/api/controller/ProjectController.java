package inha.git.project.api.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProjectController는 project 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "project controller", description = "project 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/project")
public class ProjectController {

}
