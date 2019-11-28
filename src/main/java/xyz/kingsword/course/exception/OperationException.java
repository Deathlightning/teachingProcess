package xyz.kingsword.course.exception;

import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

@Getter
public class OperationException extends BaseException {
    private ErrorEnum errorEnum;

    public OperationException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }

    public OperationException() {
        super(ErrorEnum.OPERATION_FORBIDDEN);
    }
}
