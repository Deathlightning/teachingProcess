package xyz.kingsword.course.exception;


import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;


@Getter
public class DataException extends BaseException {
    private ErrorEnum errorEnum;

    public DataException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }

    public DataException(String message) {
        super(message);
    }
}
