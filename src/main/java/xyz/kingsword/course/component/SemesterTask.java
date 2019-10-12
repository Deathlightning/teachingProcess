package xyz.kingsword.course.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.util.TimeUtil;

import java.util.Date;

/**
 * 该类通过 TimeUtil 来获取当前学期，并设置cron表达式
 */
@Component
public class SemesterTask {

    @Autowired
    private SemesterMapper semesterMapper;

    private Semester semester;

    public SemesterMapper getSemesterMapper() {
        return semesterMapper;
    }

    public Semester getSemester() {
        return semester;
    }

    /**
     * 获取开始时间的cron表达式
     *
     * @return
     */
    public String getStartSemesterCron(){
        this.semester = semesterMapper.findById(String.valueOf(TimeUtil.getNowSemesterIndex()));
        Date startDate = semester.getStartTime();
        String[] strings = startDate.toString().split("-");
        return "0 0 0 " + strings[2] + " " + strings[1] + " ?";
//        return "10 23 17 12 10 ?";
    }

    /**
     * 获取结束时间的cron表达式
     *
     * @return
     */
    public String getEndSemesterCron(){
        this.semester = semesterMapper.findById(String.valueOf(TimeUtil.getNowSemesterIndex()));
        Date startDate = semester.getEndTime();
        String[] strings = startDate.toString().split("-");
        return "0 0 0 " + strings[2] + " " + strings[1] + " ?";
//        return "20 23 17 12 10 ?";
    }

}
