package inha.git.college.service;

import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.domain.College;
import inha.git.college.domain.repository.CollegeJpaRepository;
import inha.git.college.mapper.CollegeMapper;
import inha.git.statistics.domain.CollegeStatistics;
import inha.git.statistics.domain.repository.CollegeStatisticsJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CollegeServiceImpl는 CollegeService 인터페이스를 구현하는 클래스.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CollegeServiceImpl implements CollegeService {

    private final CollegeJpaRepository collegeJpaRepository;
    private final CollegeStatisticsJpaRepository collegeStatisticsJpaRepository;
    private final CollegeMapper collegeMapper;

    @Override
    @Transactional
    public String createCollege(CreateCollegeRequest createDepartmentRequest) {
        College college = collegeJpaRepository.save
                (collegeMapper.createCollegeRequestToCollege(createDepartmentRequest));
        collegeStatisticsJpaRepository.save(collegeMapper.toCollegeStatistics(college.getId()));
        return college.getName() + " 단과대가 생성되었습니다.";
    }
}
