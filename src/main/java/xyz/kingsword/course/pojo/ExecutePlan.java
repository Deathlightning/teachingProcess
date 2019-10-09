package xyz.kingsword.course.pojo;

import lombok.Data;

@Data
public class ExecutePlan implements Comparable<ExecutePlan> {
    private String type;
    /**
     * 1选修 2必修
     */
    private int nature;
    private String courseId;
    private String courseName;
    private Float credit;
    private Float timeAll;
    private Float timeTheory;
    private Float timeLab;
    private Float timeComputer;
    private Float timeOther;
    /**
     * 周学时
     */
    private Float timeWeek;

    private int startSemester;

    private String examinationWay;

    /**
     * 专业
     */
    private String major;
    private int grade;

    @Override
    public int compareTo(ExecutePlan o) {
        return o.getCourseId().compareTo(this.getCourseId());
    }
}
