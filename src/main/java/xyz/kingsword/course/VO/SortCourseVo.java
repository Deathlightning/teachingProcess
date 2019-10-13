package xyz.kingsword.course.VO;

import lombok.Data;
import xyz.kingsword.course.util.TimeUtil;

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


    private static final long serialVersionUID = 1L;

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
        this.semesterName = TimeUtil.getSemesterName(semesterId);
    }
}
