package inha.git.admin.api.controller;

import inha.git.admin.api.controller.dto.request.*;
import inha.git.admin.api.service.AdminApproveService;
import inha.git.bug_report.api.controller.dto.response.BugReportResponse;
import inha.git.common.BaseResponse;
import inha.git.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<String> promotion(@AuthenticationPrincipal User user,
                                          @Validated @RequestBody AdminPromotionRequest adminPromotionRequest) {
        log.info("관리자로 승격 - 관리자: {}, 승격할 유저: {}", user.getName(), adminPromotionRequest.userIdx());
        return BaseResponse.of(PROMOTION_CREATED, adminApproveService.promotion(user, adminPromotionRequest));
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
    public BaseResponse<String> demotion(@AuthenticationPrincipal User user,
                                         @Validated @RequestBody AdminDemotionRequest adminDemotionRequest) {
        log.info("관리자 승격 취소 - 관리자: {}, 승격할 유저: {}", user.getName(), adminDemotionRequest.userIdx());
        return BaseResponse.of(DEMOTION_CREATED, adminApproveService.demotion(user, adminDemotionRequest));
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
    public BaseResponse<String> acceptProfessor(@AuthenticationPrincipal User user,
                                                @Validated @RequestBody ProfessorAcceptRequest professorAcceptRequest) {
        log.info("교수 승인 - 관리자: {}, 교수: {}", user.getName(), professorAcceptRequest.userIdx());
        return BaseResponse.of(PROFESSOR_ACCEPT_OK, adminApproveService.acceptProfessor(user, professorAcceptRequest));
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
    @Operation(summary = "교수 승인 취소 API(관리자 전용)", description = "교수 승인을 취소합니다.")
    public BaseResponse<String> cancelProfessor(@AuthenticationPrincipal User user,
                                                @Validated @RequestBody ProfessorCancelRequest professorCancelRequest) {
        log.info("교수 승인 취소 - 관리자: {}, 교수: {}", user.getName(), professorCancelRequest.userIdx());
        return BaseResponse.of(PROFESSOR_CANCEL_OK, adminApproveService.cancelProfessor(user, professorCancelRequest));
    }

    /**
     * 유저 차단 API
     *
     * <p>유저를 차단합니다.</p>
     *
     * @param userBlockRequest 차단할 유저 인덱스
     * @return 차단된 유저 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/block")
    @Operation(summary = "유저 차단 API(관리자 전용)", description = "유저를 차단합니다.")
    public BaseResponse<String> blockUser(@AuthenticationPrincipal User user,
                                          @Validated @RequestBody UserBlockRequest userBlockRequest) {
        log.info("유저 차단 - 관리자: {}, 차단할 유저: {}", user.getName(), userBlockRequest.userIdx());
        return BaseResponse.of(USER_BLOCK_OK, adminApproveService.blockUser(user, userBlockRequest));
    }

    /**
     * 유저 차단 해제 API
     *
     * <p>유저 차단을 해제합니다.</p>
     *
     * @param userUnblockRequest 차단 해제할 유저 인덱스
     * @return 차단 해제된 유저 정보를 포함하는 BaseResponse<String>
     */
    @PostMapping("/unblock")
    @Operation(summary = "유저 차단 해제 API(관리자 전용)", description = "유저 차단을 해제합니다.")
    public BaseResponse<String> unblockUser(@AuthenticationPrincipal User user,
                                            @Validated @RequestBody UserUnblockRequest userUnblockRequest) {
        log.info("유저 차단 해제 - 관리자: {}, 차단 해제할 유저: {}", user.getName(), userUnblockRequest.userIdx());
        return BaseResponse.of(USER_UNBLOCK_OK, adminApproveService.unblockUser(user, userUnblockRequest));
    }

    /**
     * 버그 제보 상태 변경 API
     *
     * <p>버그 제보 상태를 변경합니다.</p>
     *
     * @param bugReportId 버그 제보 ID
     * @param changeBugReportStateRequest 버그 제보 상태 변경 요청
     * @return 변경된 버그 제보 정보를 포함하는 BaseResponse<BugReportResponse>
     */
    @PostMapping("/bugReport/{bugReportId}")
    @Operation(summary = "버그 제보 상태 변경 API(관리자 전용)", description = "버그 제보 상태를 변경합니다.")
    public BaseResponse<BugReportResponse> changeBugReportState(@AuthenticationPrincipal User user,
                                                               @PathVariable("bugReportId") Integer bugReportId,
                                                               @Validated @RequestBody ChangeBugReportStateRequest changeBugReportStateRequest) {
        log.info("버그 제보 상태 변경 - 관리자: {}, 버그 제보 ID: {}", user.getName(), bugReportId);
        return BaseResponse.of(BUG_REPORT_STATE_CHANGE_OK, adminApproveService.changeBugReportState(user, bugReportId, changeBugReportStateRequest));
    }
}
