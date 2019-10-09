package xyz.kingsword.course.exception;


import lombok.Getter;
import xyz.kingsword.course.enmu.ErrorEnum;

import javax.annotation.Generated;


@Getter
public class DataException extends BaseException {
    private ErrorEnum errorEnum;

    public DataException() {
        super("数据库数据异常");
    }

    public DataException(String message) {
        super(message);
    }

    public DataException(ErrorEnum errorEnum) {
        this.errorEnum = errorEnum;
    }
}
