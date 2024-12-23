package inha.git.field.api.controller;

import inha.git.common.BaseResponse;
import inha.git.field.api.controller.dto.request.CreateFieldRequest;
import inha.git.field.api.controller.dto.request.UpdateFieldRequest;
import inha.git.field.api.controller.dto.response.SearchFieldResponse;
import inha.git.field.api.service.FieldService;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("분야 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class FieldControllerTest {

    @InjectMocks
    private FieldController fieldController;

    @Mock
    private FieldService fieldService;

    @Test
    @DisplayName("분야 전체 조회 성공")
    void getFields_Success() {
        // given
        List<SearchFieldResponse> expectedResponses = Arrays.asList(
                new SearchFieldResponse(1, "웹"),
                new SearchFieldResponse(2, "앱")
        );

        given(fieldService.getFields())
                .willReturn(expectedResponses);

        // when
        BaseResponse<List<SearchFieldResponse>> response = fieldController.getFields();

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponses);
        verify(fieldService).getFields();
    }

    @Test
    @DisplayName("분야 생성 성공")
    void createField_Success() {
        // given
        User admin = createAdminUser();
        CreateFieldRequest request = new CreateFieldRequest("신규분야");
        String expectedResponse = "신규분야 분야가 생성되었습니다.";

        given(fieldService.createField(admin, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = fieldController.createField(admin, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(fieldService).createField(admin, request);
    }

    @Test
    @DisplayName("분야명 수정 성공")
    void updateField_Success() {
        // given
        User admin = createAdminUser();
        Integer fieldIdx = 1;
        UpdateFieldRequest request = new UpdateFieldRequest("수정된분야");
        String expectedResponse = "수정된분야 분야가 수정되었습니다.";

        given(fieldService.updateField(admin, fieldIdx, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = fieldController.updateField(admin, fieldIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(fieldService).updateField(admin, fieldIdx, request);
    }

    @Test
    @DisplayName("분야 삭제 성공")
    void deleteField_Success() {
        // given
        User admin = createAdminUser();
        Integer fieldIdx = 1;
        String expectedResponse = "백엔드 분야가 삭제되었습니다.";

        given(fieldService.deleteField(admin, fieldIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = fieldController.deleteField(admin, fieldIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(fieldService).deleteField(admin, fieldIdx);
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