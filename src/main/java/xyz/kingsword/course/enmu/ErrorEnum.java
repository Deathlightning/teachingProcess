package xyz.kingsword.course.enmu;

import lombok.Getter;

/*
 * 错误信息，命名规范：
 * error单词写后面，用注释对错误进行分类，每类留10个空间
 * */
@Getter
public enum ErrorEnum {

    /**
     * 其他
     */
    ERROR_LOGIN(400, "账号或密码不正确"),
    UN_LOGIN(401, "尚未登陆"),
    NO_AUTH(402, "没有权限"),
    ERROR_PARAMETER(403, "参数异常"),
    ERROR_FILE(405, "文件上传异常"),
    OPERATION_FORBIDDEN(406, "操作禁止"),

    /**
     * 排课
     */
    DIFFERENT_COURSE(406, "不同课程无法合并"),
    DIFFERENT_TEACHER(407, "不同教师无法合并"),
    SINGLE_DATA(408, "单条数据无法合并"),
    /**
     * 培养方案执行计划
     */
    TRAINING_PROGRAM_ERROR(421, "培养方案未上传"),
    EXECUTION_PLAN_ERROR(422, "执行计划未上传"),
    //    专业，年级，课程，执行学期不同导致
    VERIFY_ERROR(423, "系统内部错误,无法验证"),

    /**
     * 编码可能导致的问题
     */

    ERROR(500, "系统内部异常"),
    DATA_ERROR(501, "数据库数据异常");

    private Integer code;

    private String msg;

    ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}