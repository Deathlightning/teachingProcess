package xyz.kingsword.course.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Course {
    //************************************
//    课程基本信息
//****************************************
    private String id;

    private String name;

    private String teacherInCharge;

    /**
     * see:{@link xyz.kingsword.course.enmu.CourseTypeEnum}
     */
    private int type;

    /**
     * 是否核心课程
     */
    private boolean core;

    /**
     * 1选修2必修
     */
    private int nature;

    private int credit;

    /**
     * 院考系考
     */
    private String collegesOrDepartments;

    /**
     * 考试类型 考查还是考试
     */
    private String examinationWay;


    private String textBook;

    private String referenceBook;

    private List<Book> bookList;

    private List<Book> referenceBookList;

    private int researchRoomId;

    /**
     * 辅导时间地点
     */
    private String coach;
    /**
     * 辅导教师
     */
    private String coachTeacher;

//****************************************
//    学时组成
//****************************************

    /**
     * 周学时
     */
    private int timeWeek;
    /**
     * 上课周数
     */
    private int weekNum;

    /**
     * 理论学时
     */
    private int timeTheory;
    /**
     * 理教周数
     */
    private int theoryWeek;

    /**
     * 实践学时
     */
    private int timePractical;

    /**
     * 上机学时
     */

    private int timeComputer;

    /**
     * 实验学时
     */
    private int timeLab;

    /**
     * 习题课学时
     */
    private int timeHomework;

    /**
     * 总学时
     */
    private int timeAll;


    /**
     * 期末考核方式
     * see:{@link xyz.kingsword.course.enmu.AssessmentEnum}
     */
    private int assessmentWay;


//*****************************************
//      成绩组成
//*****************************************


    private int homeworkProportion;

    private Double testProportion;

    private Double labProportion;

    private Double attendanceProportion;

    private Double performanceProportion;

    /**
     * 小论文或综合作业
     */
    private Double paperProportion;

    private Double otherProportion;

    private Double examProportion;
}