package xyz.kingsword.course.service;

import com.deepoove.poi.XWPFTemplate;
import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.TeacherGroup;

import java.util.List;

public interface CalendarService {
    /**
     * 查本课程组所有的教学日历
     *
     * @param courseId 课程id
     * @return List<Calendar>
     */
    List<Calendar> selectOnCourse(String courseId);

    /**
     * 查单个教学日历
     *
     * @param sortId 排课id
     * @param teaId  教师id
     * @return Calendar
     */
    Calendar selectOne(int sortId, String teaId);

    /**
     * 查单个教学日历
     *
     * @param id 教学日历id
     * @return Calendar
     */
    Calendar selectOne(int id);


    int insert(Calendar calendar);

    int update(Calendar calendar);

    /**
     * 教学日历审核，由教研室主任进行审核，考虑课程负责人进行一级审核所以要传角色id
     *
     * @param ids    教学日历id
     * @param roleId 角色id
     */
    void verify(List<Integer> ids, int roleId);

    PageInfo<TeacherGroup> getCourseGroupByResearch(int researchRoomId, String semesterId, int pageNum, int pageSize);


    XWPFTemplate export(int calendarId);

    PageInfo<Calendar> getVerifyStatus(String courseId, String semesterId, int pageNum, int pageSize);

    /**
     * 复制教学日历，仅需本课程组
     *
     * @param id       被复制教学日历id
     * @param teaId    执行复制操作的教师id
     * @param courseId 所属课程id
     */
    void copy(int id, String teaId, String courseId);
}
