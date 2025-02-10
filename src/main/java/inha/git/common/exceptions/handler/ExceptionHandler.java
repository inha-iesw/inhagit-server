package inha.git.common.exceptions.handler;

import inha.git.common.code.BaseErrorCode;
import inha.git.common.exceptions.BaseException;

public class ExceptionHandler extends BaseException {
    public ExceptionHandler(BaseErrorCode errorCode) {super(errorCode);}
}
