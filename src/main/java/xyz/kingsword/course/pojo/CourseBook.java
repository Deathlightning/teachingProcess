package xyz.kingsword.course.pojo;

import lombok.Data;

/**
 * 导出书籍订购信息需要用的课程信息
 */
@Data
public class CourseBook {
    private String courseId;
    private String courseName;
    private int nature;
    private String className;
    private String textBook;
}
