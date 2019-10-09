package xyz.kingsword.course.exception;

import cn.hutool.core.exceptions.ValidateException;
import lombok.Data;
import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

@Getter
public class AuthException extends BaseException {

    private ErrorEnum errorEnum;

    public AuthException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }


    public AuthException(String message) {
        super(message);
    }

    public AuthException() {

    }
}
