package xyz.kingsword.course.exception;

import xyz.kingsword.course.enmu.ErrorEnum;

public class BaseException extends RuntimeException {
    private ErrorEnum errorEnum;

    public BaseException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }

    public BaseException(String message) {
        super(message);
    }


    public BaseException() {
        super();
    }

    public ErrorEnum getErrorEnum() {
        return errorEnum == null ? ErrorEnum.ERROR : errorEnum;
    }
}
