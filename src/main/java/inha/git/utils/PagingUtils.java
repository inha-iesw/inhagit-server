package inha.git.utils;

import inha.git.common.exceptions.BaseException;
import org.springframework.stereotype.Component;

import static inha.git.common.code.status.ErrorStatus.INVALID_PAGE;

@Component
public class PagingUtils {

    private static final int MIN_PAGE = 1;

    public void validatePage(int page) {
        if (page < MIN_PAGE) {
            throw new BaseException(INVALID_PAGE);
        }
    }

    public int toPageIndex(int page) {
        return page - 1;
    }
}