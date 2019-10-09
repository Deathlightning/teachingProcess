package xyz.kingsword.course.enmu;

import lombok.Getter;

/**
 * 课程类别枚举类
 */
@Getter
public enum CourseTypeEnum {
    GENERAL_EDUCATION(1, "通识教育课程"),
    PROFESSIONAL_LESSON(2, "专业课程"),
    SUBJECT_REQUIRED(3, "学科必修课"),
    PRACTICE(4, "实践环节");

    private int code;
    private String content;

    CourseTypeEnum(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public static CourseTypeEnum getContent(int code) {
        CourseTypeEnum val = null;
        for (CourseTypeEnum courseTypeEnum : CourseTypeEnum.values()) {
            if (courseTypeEnum.getCode() == code) {
                val = courseTypeEnum;
                break;
            }
        }
        return val;
    }
}
