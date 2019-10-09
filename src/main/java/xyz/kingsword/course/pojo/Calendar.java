package xyz.kingsword.course.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wzh
 */
@Data
public class Calendar implements Serializable {
    private Integer id;

    private String teaId;


    /**
     * 课程id
     */
    private String courseId;


    /**
     * 排课id
     */
    private Integer sortId;

    /**
     * 理教周数
     */
    private Integer weekOfTheory;

    /**
     * 习题课学时
     */
    private Integer timeOfHomework;

    /**
     * 总学时
     */
    private Integer timeOfAll;

    /**
     * 必修、限选、任选
     */
    private String property;

    /**
     * 小论文或综合作业
     */
    private Integer paperProportion;

    /**
     * 出勤占比
     */
    private Double attendanceProportion;

    /**
     * 课堂表现占比
     */
    private Double performanceProportion;

    /**
     * 其他方式占比
     */
    private Double otherProportion;

    /**
     * 辅导答疑时间地点
     */
    private String coach;

    /**
     * 授课内容
     */
    private String teachingContent;

    /**
     * 0正常1课程组长审核通过2教研室主任审核通过
     */
    private Integer status;

    /**
     * 1闭卷笔试2口试3综合实验4开卷笔试5论文6其他
     */
    private Integer examinationWay;

    /**
     * 学期id
     */
    private String semesterId;

    /**
     * 教学日历生成中的时间规则
     */
    private String timeRule;

    private static final long serialVersionUID = 1L;

    /**
     * 这个字段不要随便用,不在calendar表
     */
    private String teaName;
}

