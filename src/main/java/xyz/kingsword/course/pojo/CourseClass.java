package xyz.kingsword.course.pojo;

import lombok.Data;

/**
 * 课程班级信息，按学期展示课程名称，课程号，上课班级
 * 对应视图course_info_view
 */
@Data
public class CourseClass {
    private String courseId;
    private String courseName;
    private String className;
    private String semesterId;
}
