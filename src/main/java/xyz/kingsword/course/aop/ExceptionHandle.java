package xyz.kingsword.course.aop;

import cn.hutool.core.exceptions.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.pojo.Result;

/**
 * 拦截异常，向前端返回错误信息
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = AuthException.class)
    @ResponseBody
    public Result exceptionGet(AuthException e) {
        log.error("【权限异常】{}", e.getMessage());
        return new Result(e.getErrorEnum());
    }

    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public Result exceptionGet(BaseException e) {
        return new Result(-1, e.getMessage());
    }


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionGet(Exception e) {
        log.error("【系统异常】{}", e.getMessage());
        e.printStackTrace();
        return new Result(-1, "系统异常");
    }
}
