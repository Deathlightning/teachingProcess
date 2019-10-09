package xyz.kingsword.course.exception;

import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

@Getter
public class BaseException extends RuntimeException {
    private ErrorEnum errorEnum;

    public BaseException(ErrorEnum errorEnum) {

    }

    public BaseException(String message) {
        super(message);
    }

    public BaseException() {
        super();
    }
}
