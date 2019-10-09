package xyz.kingsword.course.exception;

import cn.hutool.core.exceptions.ValidateException;


public class AuthException extends ValidateException {
    public AuthException() {

    }

    public AuthException(String message) {
        super(message);
    }
}
