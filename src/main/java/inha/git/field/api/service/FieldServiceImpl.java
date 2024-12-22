package inha.git.field.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.api.mapper.FieldMapper;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;

/**
 * FieldService 인터페이스를 구현하는 서비스 클래스입니다.
 * 분야의 조회, 생성, 수정, 삭제 등의 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FieldServiceImpl implements FieldService {

    private final FieldJpaRepository fieldJpaRepository;
    private final FieldMapper fieldMapper;

    /**
     * 활성화된 모든 분야를 조회합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. ACTIVE 상태의 모든 분야를 조회<br>
     * 2. 조회된 분야 엔티티들을 DTO로 변환<br>
     * </p>
     *
     * @return 분야 정보 목록 (SearchFieldResponse)
     */
    @Override
    public List<SearchFieldResponse> getFields() {
        return fieldMapper.fieldsToSearchFieldResponses(fieldJpaRepository.findAllByState(ACTIVE));
    }

    /**
     * 새로운 분야를 생성합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. 요청 DTO를 분야 엔티티로 변환<br>
     * 2. 분야 엔티티 저장<br>
     * 3. 생성 결과 메시지 반환<br>
     * </p>
     *
     * @param admin 생성을 요청한 관리자 정보
     * @param createFieldRequest 생성할 분야 정보
     * @return 분야 생성 완료 메시지
     */
    @Override
    @Transactional
    public String createField(User admin, CreateFieldRequest createFieldRequest) {
        Field field = fieldMapper.createFieldRequestToField(createFieldRequest);
        Field savedField = fieldJpaRepository.save(field);
        log.info("분야 생성 성공 - 관리자: {} 분야명: {}", admin.getName(), field.getName());
        return savedField.getName() + " 분야가 생성되었습니다.";
    }

    /**
     * 분야명을 수정합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. ID와 상태로 분야 조회<br>
     * 2. 분야 존재 여부 확인<br>
     * 3. 분야명 수정<br>
     * 4. 수정 결과 메시지 반환<br>
     * </p>
     *
     * @param admin 수정을 요청한 관리자 정보
     * @param fieldIdx 수정할 분야의 식별자
     * @param updateFieldRequest 새로운 분야명 정보
     * @return 분야명 수정 완료 메시지
     * @throws BaseException FIELD_NOT_FOUND: 분야를 찾을 수 없는 경우
     */
    @Override
    public String updateField(User admin, Integer fieldIdx, UpdateFieldRequest updateFieldRequest) {
        Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        field.setName(updateFieldRequest.name());
        log.info("분야 수정 성공 - 관리자: {} 분야명: {}", admin.getName(), field.getName());
        return field.getName() + " 분야가 수정되었습니다.";
    }

    /**
     * 분야를 삭제(비활성화) 처리합니다.
     *
     * <p>
     * 처리 과정:<br>
     * 1. ID와 상태로 분야 조회<br>
     * 2. 분야 존재 여부 확인<br>
     * 3. 분야 상태를 INACTIVE로 변경<br>
     * 4. 삭제 시간 기록<br>
     * 5. 삭제 결과 메시지 반환<br>
     * </p>
     *
     * @param admin 삭제를 요청한 관리자 정보
     * @param fieldIdx 삭제할 분야의 식별자
     * @return 분야 삭제 완료 메시지
     * @throws BaseException FIELD_NOT_FOUND: 분야를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public String deleteField(User admin, Integer fieldIdx) {
        Field field = fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE)
                .orElseThrow(() -> new BaseException(FIELD_NOT_FOUND));
        field.setState(INACTIVE);
        field.setDeletedAt();
        log.info("분야 삭제 성공 - 관리자: {} 분야명: {}", admin.getName(), field.getName());
        return field.getName() + " 분야가 삭제되었습니다.";
    }
}
