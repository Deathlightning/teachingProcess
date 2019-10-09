package xyz.kingsword.course.service.impl;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.CalendarMapper;
import xyz.kingsword.course.dao.CourseGroupMapper;
import xyz.kingsword.course.dao.DO.CalendarDataDO;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.CourseGroup;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.service.CalendarService;
import xyz.kingsword.course.service.calendarExport.CalendarData;
import xyz.kingsword.course.service.calendarExport.TableRenderPolicy;
import xyz.kingsword.course.util.Constant;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CalendarServiceImpl implements CalendarService {
    @Autowired
    private CalendarMapper calendarMapper;
    @Autowired
    private SemesterMapper semesterMapper;
    @Autowired
    private CourseGroupMapper courseGroupMapper;

    @Override
    public List<Calendar> selectOnCourse(String courseId) {
        return null;
    }

    @Override
    public Calendar selectOne(int sortId, String teaId) {
        return null;
    }

    @Override
    public Calendar selectOne(int id) {
        return calendarMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(Calendar calendar) {
        return calendarMapper.insert(calendar);
    }

    @Override
    public int update(Calendar calendar) {
        return calendarMapper.updateByPrimaryKeySelective(calendar);
    }

    @Override
    public void verify(List<Integer> ids, int roleId) {
        int status;
        switch (roleId) {
            //课程负责人审核
            case 4:
                status = 2;
                break;
            //教研室主任审核
            case 5:
                status = 1;
                break;
            default:
                status = 0;

        }
        calendarMapper.setStatus(ids, status);
    }

    @Override
    public PageInfo<TeacherGroup> getCourseGroupByResearch(int researchRoomId, String semesterId, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> calendarMapper.getCourseGroupByResearch(researchRoomId, semesterId));

    }

    @Override
    public XWPFTemplate export(int calendarId) {
        CalendarData calendarData = renderExportData(calendarId);
        Constant.threadLocal.set(calendarData);
        //读取模版
        ClassPathResource resource = new ClassPathResource("word/calendar.docx");
        Configure config = Configure.newBuilder().customPolicy("tableData", new TableRenderPolicy()).build();
        return XWPFTemplate.compile(resource.getStream(), config).render(calendarData);
    }

    @Override
    public PageInfo<Calendar> getVerifyStatus(String courseId, String semesterId, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> calendarMapper.getVerifyStatus(courseId, semesterId));
    }

    @Override
    public void copy(int id, String teaId, String courseId) {
        Calendar calendar = calendarMapper.selectByPrimaryKey(id);
        List<CourseGroup> courseGroupList = courseGroupMapper.select(null, null, courseId, teaId);
        Validator.validateFalse(courseGroupList.isEmpty(), "数据库错误");
        CourseGroup courseGroup = courseGroupList.get(0);
        Validator.validateTrue(courseGroup.getCalendarId() == null, "您已填写教学日历");
        boolean flag = ObjectUtil.equal(courseId, calendar.getCourseId());
        Validator.validateTrue(flag, "不属于同一课程组");
        calendar.setTeaId(teaId);
        calendar.setSortId(courseGroup.getSortId());
        calendarMapper.insert(calendar);
    }


    private CalendarData renderExportData(int calendarId) {
        CalendarDataDO calendarDataDO = calendarMapper.exportCalendar(calendarId);
        Optional.ofNullable(calendarDataDO).orElseThrow(() -> new DataException("教学日历导出错误，id：" + calendarId));
        return new CalendarData(calendarDataDO);
    }
}
