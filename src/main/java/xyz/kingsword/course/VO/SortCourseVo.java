package xyz.kingsword.course.VO;

import lombok.Data;

@Data
public class SortCourseVo {
    private Integer id;

    private String teacherId;

    private String teacherName;

    private String courseId;

    private String courseName;

    private String nature;

    /**
     * 学生总数
     */
    private Integer studentNum;

    private Integer classroomId;

//    private String classroomName;

    /**
     * 学期id
     */
    private String semesterId;

    private String semesterName;

    private String className;
    private String classId;


    private static final long serialVersionUID = 1L;
}
