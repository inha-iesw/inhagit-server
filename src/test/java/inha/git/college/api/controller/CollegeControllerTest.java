package inha.git.college.api.controller;

import inha.git.college.controller.CollegeController;
import inha.git.college.controller.dto.request.CreateCollegeRequest;
import inha.git.college.controller.dto.request.UpdateCollegeRequest;
import inha.git.college.controller.dto.response.SearchCollegeResponse;
import inha.git.college.service.CollegeService;
import inha.git.common.BaseResponse;
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

@DisplayName("단과대 컨트롤러 테스트")
@ExtendWith(MockitoExtension.class)
class CollegeControllerTest {

    @InjectMocks
    private CollegeController collegeController;

    @Mock
    private CollegeService collegeService;

    @Test
    @DisplayName("단과대 전체 조회 성공")
    void getColleges_Success() {
        // given
        List<SearchCollegeResponse> expectedResponses = Arrays.asList(
                new SearchCollegeResponse(1, "소프트웨어융합대학"),
                new SearchCollegeResponse(2, "공과대학")
        );

        given(collegeService.getColleges())
                .willReturn(expectedResponses);

        // when
        BaseResponse<List<SearchCollegeResponse>> response = collegeController.getColleges();

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponses);
        verify(collegeService).getColleges();
    }

    @Test
    @DisplayName("특정 단과대 조회 성공")
    void getCollege_Success() {
        // given
        Integer departmentIdx = 1;
        SearchCollegeResponse expectedResponse = new SearchCollegeResponse(1, "소프트웨어융합대학");


        given(collegeService.getCollege(departmentIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<SearchCollegeResponse> response = collegeController.getCollege(departmentIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(collegeService).getCollege(departmentIdx);
    }

    @Test
    @DisplayName("단과대 생성 성공")
    void createCollege_Success() {
        // given
        User admin = createAdminUser();
        CreateCollegeRequest request = new CreateCollegeRequest("신설단과대학");
        String expectedResponse = "신설단과대학 단과대가 생성되었습니다.";

        given(collegeService.createCollege(admin, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = collegeController.createCollege(admin, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(collegeService).createCollege(admin, request);
    }

    @Test
    @DisplayName("단과대 수정 성공")
    void updateCollege_Success() {
        // given
        User admin = createAdminUser();
        Integer collegeIdx = 1;
        UpdateCollegeRequest request = new UpdateCollegeRequest("수정된단과대학");
        String expectedResponse = "수정된단과대학 단과대 이름이 변경되었습니다.";

        given(collegeService.updateCollegeName(admin, collegeIdx, request))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = collegeController.updateCollege(admin, collegeIdx, request);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(collegeService).updateCollegeName(admin, collegeIdx, request);
    }

    @Test
    @DisplayName("단과대 삭제 성공")
    void deleteCollege_Success() {
        // given
        User admin = createAdminUser();
        Integer collegeIdx = 1;
        String expectedResponse = "IT공과대학 단과대가 삭제되었습니다.";

        given(collegeService.deleteCollege(admin, collegeIdx))
                .willReturn(expectedResponse);

        // when
        BaseResponse<String> response = collegeController.deleteCollege(admin, collegeIdx);

        // then
        assertThat(response.getResult()).isEqualTo(expectedResponse);
        verify(collegeService).deleteCollege(admin, collegeIdx);
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