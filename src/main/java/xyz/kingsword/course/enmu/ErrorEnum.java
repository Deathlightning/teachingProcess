package xyz.kingsword.course.enmu;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    /*
     * 错误信息
     * */
    ERROR_LOGIN(400, "账号或密码不正确"),
    UN_LOGIN(401, "尚未登陆"),
    ERROR_PARAMETER(402, "参数异常"),
    NO_AUTH(403, "没有权限"),
    OPERATION_ERROR(405, "您不能在此执行此操作"),
    ERROR(500, "系统内部异常");

    private Integer code;

    private String msg;

    ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}