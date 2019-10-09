package xyz.kingsword.course.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 执行计划
 *
 * @author wzh
 */
@Data
public class TrainingProgram implements Serializable, Comparable<TrainingProgram> {
    /**
     * id
     */
    private Integer id;

    /**
     * 课程id
     */
    private String courseId;

    /**
     * 课程名
     */
    private String courseName;

    /**
     * 学分
     */
    private Float credit;

    /**
     * 是否核心课程
     */
    private Boolean core;

    /**
     * 考核方式（考试考查）
     */
    private String examinationWay;

    /**
     * 院考或系考
     */
    private String collegesOrDepartments;

    /**
     * 总学时
     */
    private Float timeAll;

    /**
     * 理论学时
     */
    private Float timeTheory=0F;

    /**
     * 实验学时
     */
    private Float timeLab = 0f;

    /**
     * 实践学时
     */
    private Float timePractical = 0f;

    /**
     * 上机学时
     */
    private Float timeComputer = 0f;

    /**
     * 其他学时
     */
    private Float timeOther = 0f;

    /**
     * 起始学期
     */
    private Integer startSemester;

    /**
     * 培养方案按入学时间分，如：2017级本科培养方案，2018级本科培养方案
     * eg:2017 2018
     */
    private int grade;

    private static final long serialVersionUID = 1L;

    @Override
    public int compareTo(TrainingProgram o) {
        return o.getCourseId().compareTo(this.getCourseId());
    }
}