package inha.git.common.exceptions;

import inha.git.common.code.BaseErrorCode;
import inha.git.common.code.ErrorReasonDTO;
import inha.git.common.code.status.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseException extends RuntimeException {
    private BaseErrorCode code;

    public BaseException(ErrorStatus status) {
        super(status.getMessage()); // 여기 추가
        this.code = status;
    }

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
