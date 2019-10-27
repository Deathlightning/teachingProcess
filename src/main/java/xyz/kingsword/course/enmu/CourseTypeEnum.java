package xyz.kingsword.course.enmu;

import lombok.Getter;

/**
 * 课程类别枚举类
 */
@Getter
public enum CourseTypeEnum {
    GENERAL_EDUCATION_REQUIRED(1, "通识教育必修课"),
    GENERAL_EDUCATION(2, "通识教育选修课"),
    PROFESSIONAL_LESSON_REQUIRED(3, "专业必修课"),
    PROFESSIONAL_LESSON(4, "专业选修课"),
    SUBJECT_REQUIRED(5, "学科必修课"),
    PRACTICE(6, "实践环节");

    private int code;
    private String content;

    CourseTypeEnum(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public static CourseTypeEnum get(int code) {
        CourseTypeEnum val = null;
        for (CourseTypeEnum courseTypeEnum : CourseTypeEnum.values()) {
            if (courseTypeEnum.getCode() == code) {
                val = courseTypeEnum;
                break;
            }
        }
        return val;
    }

    public static CourseTypeEnum get(String content) {
        CourseTypeEnum val = null;
        for (CourseTypeEnum courseTypeEnum : CourseTypeEnum.values()) {
            if (courseTypeEnum.getContent().equals(content)) {
                val = courseTypeEnum;
                break;
            }
        }
        return val;
    }
}
