package inha.git.college.controller;

import inha.git.college.service.CollegeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CollegeController는 collage 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "collage controller", description = "collage 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/collages")
public class CollegeController {

    private final CollegeService collegeService;


}
