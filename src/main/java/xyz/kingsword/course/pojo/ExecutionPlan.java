package xyz.kingsword.course.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * execution_plan
 * @author 
 */
@Data
public class ExecutionPlan implements Serializable {
    private Integer id;

    private Integer type;

    private Integer nature;

    private String courseId;

    private String courseName;

    private Integer credit;

    private Integer timeAll;

    private Integer timeTheory;

    private Integer timeLab;

    private Integer timeComputer;

    private Integer timeOther;

    private Integer timeWeek;

    /**
     * 开课学期
     */
    private Integer startSemester;

    private String examinationWay;

    /**
     * 专业id
     */
    private Integer specialityId;

    /**
     * 计划执行学期id
     */
    private String semesterId;

    private static final long serialVersionUID = 1L;

}