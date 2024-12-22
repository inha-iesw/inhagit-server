package inha.git.field.api.service;

import inha.git.common.exceptions.BaseException;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.api.mapper.FieldMapper;
import inha.git.field.domain.Field;
import inha.git.field.domain.repository.FieldJpaRepository;
import inha.git.user.domain.User;
import inha.git.user.domain.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static inha.git.common.BaseEntity.State.ACTIVE;
import static inha.git.common.BaseEntity.State.INACTIVE;
import static inha.git.common.code.status.ErrorStatus.FIELD_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    @InjectMocks
    private FieldServiceImpl fieldService;

    @Mock
    private FieldJpaRepository fieldJpaRepository;

    @Mock
    private FieldMapper fieldMapper;

    @Test
    @DisplayName("분야 전체 조회 성공")
    void getFields_Success() {
        // given
        List<Field> fields = Arrays.asList(
                createField(1, "웹"),
                createField(2, "앱")
        );
        List<SearchFieldResponse> expectedResponses = Arrays.asList(
                new SearchFieldResponse(1, "웹"),
                new SearchFieldResponse(2, "앱")
        );

        given(fieldJpaRepository.findAllByState(ACTIVE))
                .willReturn(fields);
        given(fieldMapper.fieldsToSearchFieldResponses(fields))
                .willReturn(expectedResponses);

        // when
        List<SearchFieldResponse> result = fieldService.getFields();

        // then
        assertThat(result).isEqualTo(expectedResponses);
        verify(fieldJpaRepository).findAllByState(ACTIVE);
    }

    @Test
    @DisplayName("분야 생성 성공")
    void createField_Success() {
        // given
        User admin = createAdminUser();
        CreateFieldRequest request = new CreateFieldRequest("신규분야");
        Field field = createField(1, "신규분야");

        given(fieldMapper.createFieldRequestToField(request))
                .willReturn(field);
        given(fieldJpaRepository.save(any(Field.class)))
                .willReturn(field);

        // when
        String result = fieldService.createField(admin, request);

        // then
        assertThat(result).isEqualTo("신규분야 분야가 생성되었습니다.");
        verify(fieldJpaRepository).save(any(Field.class));
    }

    @Test
    @DisplayName("분야명 수정 성공")
    void updateField_Success() {
        // given
        User admin = createAdminUser();
        Integer fieldIdx = 1;
        UpdateFieldRequest request = new UpdateFieldRequest("수정된분야");
        Field field = createField(fieldIdx, "기존분야");

        given(fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE))
                .willReturn(Optional.of(field));

        // when
        String result = fieldService.updateField(admin, fieldIdx, request);

        // then
        assertThat(result).isEqualTo("수정된분야 분야가 수정되었습니다.");
        assertThat(field.getName()).isEqualTo("수정된분야");
    }

    @Test
    @DisplayName("존재하지 않는 분야 수정 시 예외 발생")
    void updateField_NotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer fieldIdx = 999;
        UpdateFieldRequest request = new UpdateFieldRequest("수정된분야");

        given(fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                fieldService.updateField(admin, fieldIdx, request));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(FIELD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("분야 삭제 성공")
    void deleteField_Success() {
        // given
        User admin = createAdminUser();
        Integer fieldIdx = 1;
        Field field = createField(fieldIdx, "삭제할분야");

        given(fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE))
                .willReturn(Optional.of(field));

        // when
        String result = fieldService.deleteField(admin, fieldIdx);

        // then
        assertThat(result).isEqualTo("삭제할분야 분야가 삭제되었습니다.");
        assertThat(field.getState()).isEqualTo(INACTIVE);
        assertThat(field.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 분야 삭제 시 예외 발생")
    void deleteField_NotFound_ThrowsException() {
        // given
        User admin = createAdminUser();
        Integer fieldIdx = 999;

        given(fieldJpaRepository.findByIdAndState(fieldIdx, ACTIVE))
                .willReturn(Optional.empty());

        // when & then
        BaseException exception = assertThrows(BaseException.class, () ->
                fieldService.deleteField(admin, fieldIdx));

        assertThat(exception.getErrorReason().getMessage())
                .isEqualTo(FIELD_NOT_FOUND.getMessage());
    }

    private Field createField(Integer id, String name) {
        return Field.builder()
                .id(id)
                .name(name)
                .build();
    }

    private User createAdminUser() {
        return User.builder()
                .id(1)
                .email("admin@test.com")
                .name("관리자")
                .role(Role.ADMIN)
                .build();
    }
}