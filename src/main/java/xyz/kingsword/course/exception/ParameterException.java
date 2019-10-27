package xyz.kingsword.course.exception;

import xyz.kingsword.course.enmu.ErrorEnum;

public class ParameterException extends BaseException {
    private ErrorEnum errorEnum;


    public ParameterException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }
}
