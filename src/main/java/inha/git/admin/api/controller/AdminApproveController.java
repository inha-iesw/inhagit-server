package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.request.*;
import inha.git.admin.api.service.AdminApproveService;
import inha.git.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static inha.git.common.code.status.SuccessStatus.*;

/**
 * AdminApproveController는 관리자 전용 계정 조회 관련 엔드포인트를 처리.
 */
@Slf4j
@Tag(name = "admin approve controller", description = "admin approve 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin")
public class AdminApproveController {

    private final AdminApproveService adminApproveService;

    /**
     * 관리자로 승격 API
     *
     * <p>유저를 관리자로 승격합니다.</p>
     *
     * @param adminPromotionRequest 관리자로 승격할 유저 인덱스
     * @return 승격된 유저 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/promotion")
    @Operation(summary = "관리자로 승격 API(관리자 전용)", description = "유저를 관리자로 승격합니다.")
    public BaseResponse<String> promotion(@Validated @RequestBody AdminPromotionRequest adminPromotionRequest) {
        return BaseResponse.of(PROMOTION_CREATED, adminApproveService.promotion(adminPromotionRequest));
    }

    /**
     * 관리자 승격 취소 API
     *
     * <p>관리자 승격을 취소합니다.</p>
     *
     * @param adminDemotionRequest 관리자 승격 취소할 유저 인덱스
     * @return 승격 취소된 유저 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/demotion")
    @Operation(summary = "관리자 승격 취소 API(관리자 전용)", description = "관리자 승격을 취소합니다.")
    public BaseResponse<String> demotion(@Validated @RequestBody AdminDemotionRequest adminDemotionRequest) {
        return BaseResponse.of(DEMOTION_CREATED, adminApproveService.demotion(adminDemotionRequest));
    }

    /**
     * 교수 승인 API
     *
     * <p>교수 승인을 합니다.</p>
     *
     * @param professorAcceptRequest 교수 승인할 유저 인덱스
     * @return 승인된 교수 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/professor/accept")
    @Operation(summary = "교수 승인 API(관리자 전용)", description = "교수 승인을 합니다.")
    public BaseResponse<String> acceptProfessor(@Validated @RequestBody ProfessorAcceptRequest professorAcceptRequest) {
        return BaseResponse.of(PROFESSOR_ACCEPT_OK, adminApproveService.acceptProfessor(professorAcceptRequest));
    }

    /**
     * 교수 승인 취소 API
     *
     * <p>교수 승인을 취소합니다.</p>
     *
     * @param professorCancelRequest 교수 승인 취소할 유저 인덱스
     * @return 승인 취소된 교수 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/professor/cancel")
    @Operation(summary = "교수 승인 취소 API", description = "교수 승인을 취소합니다.")
    public BaseResponse<String> cancelProfessor(@Validated @RequestBody ProfessorCancelRequest professorCancelRequest) {
        return BaseResponse.of(PROFESSOR_DEMOTE_OK, adminApproveService.cancelProfessor(professorCancelRequest));
    }

}
