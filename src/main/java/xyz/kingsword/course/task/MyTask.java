package xyz.kingsword.course.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.component.SemesterTask;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.pojo.Semester;

@RestController
@EnableScheduling
public class MyTask implements SchedulingConfigurer {

    @Autowired
    private SemesterTask semesterTask;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {

        /**
         * 学期开始
         */
        scheduledTaskRegistrar.addCronTask(
                new Runnable() {
                    @Override
                    public void run() {
                        Semester semester = semesterTask.getSemester();
                        //修改学期状态？
                        System.out.println("学期开始！");
                        semester.setStatus(0);
                        SemesterMapper semesterMapper = semesterTask.getSemesterMapper();
                        semesterMapper.updateById(semester);
                    }
                },
                semesterTask.getStartSemesterCron()
        );

        /**
         * 学期结束
         */
        scheduledTaskRegistrar.addCronTask(
                new Runnable() {
                    @Override
                    public void run() {
                        Semester semester = semesterTask.getSemester();
                        //修改学期状态
                        System.out.println("学期结束！");
                        semester.setStatus(1);
                        SemesterMapper semesterMapper = semesterTask.getSemesterMapper();
                        semesterMapper.updateById(semester);
                    }
                },
                semesterTask.getEndSemesterCron()
        );
    }
}
