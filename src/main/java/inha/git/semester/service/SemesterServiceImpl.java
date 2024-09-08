package inha.git.semester.service;

import inha.git.semester.domain.repository.SemesterJpaRepository;
import inha.git.semester.mapper.SemesterMapper;
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
public class SemesterServiceImpl implements SemesterService {

    private final SemesterJpaRepository semesterJpaRepository;
    private final SemesterMapper semesterMapper;


}
