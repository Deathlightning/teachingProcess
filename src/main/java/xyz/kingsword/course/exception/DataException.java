package xyz.kingsword.course.exception;

import cn.hutool.core.exceptions.ValidateException;

public class DataException extends ValidateException {
    public DataException() {
        super("数据库数据异常");
    }

    public DataException(String message) {
        super(message);
    }
}
