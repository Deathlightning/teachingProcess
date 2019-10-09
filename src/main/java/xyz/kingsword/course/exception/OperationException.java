package xyz.kingsword.course.exception;

import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

@Getter
public class OperationException extends BaseException {
    private ErrorEnum errorEnum;

    public OperationException() {
        super("您的操作不对");
    }

    public OperationException(String message) {
        super(message);
    }

    public OperationException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }
}
