package xyz.kingsword.course.aop;

import cn.hutool.core.exceptions.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.pojo.Result;

/**
 * 拦截异常，向前端返回错误信息
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = AuthException.class)
    public Result exceptionGet(AuthException e) {
        log.error("【权限异常】{}", e.getMessage());
        return new Result();
    }

    @ExceptionHandler(value = ValidateException.class)
    public String exceptionGet(ValidateException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        log.error("【系统异常】{}", e.getMessage());
        e.printStackTrace();
        return "error/errorMessage";
    }


    @ExceptionHandler(value = Exception.class)
    public String exceptionGet(Exception e) {
        log.error("【系统异常】{}", e.getMessage());
        e.printStackTrace();
        return "error/500";
    }
}
