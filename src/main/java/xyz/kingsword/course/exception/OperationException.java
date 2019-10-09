package xyz.kingsword.course.exception;

import cn.hutool.core.exceptions.ValidateException;

public class OperationException extends ValidateException {
    public OperationException() {
        super("您的操作不对");
    }

    public OperationException(String message) {
        super(message);
    }
}
