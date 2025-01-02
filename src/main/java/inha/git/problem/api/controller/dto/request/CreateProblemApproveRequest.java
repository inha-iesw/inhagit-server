package inha.git.problem.api.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
public record CreateProblemApproveRequest(

        @NotNull
        @Schema(description = "문제 신청 인덱스", example = "1")
        Integer requestIdx

) { }
