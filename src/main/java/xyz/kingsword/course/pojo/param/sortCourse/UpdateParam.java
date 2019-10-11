package xyz.kingsword.course.pojo.param.sortCourse;

import lombok.Data;

/**
 * 排课表更新参数，给老师排课，或给课程排老师
 */
@Data
public class UpdateParam {
    private Integer id;
    private String teacherId;
    private String courseId;
}