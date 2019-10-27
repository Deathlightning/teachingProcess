package xyz.kingsword.course.controller;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.XWPFTemplate;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.VO.CourseVo;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.pojo.Calendar;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.*;
import xyz.kingsword.course.service.calendarExport.CalendarData;
import xyz.kingsword.course.util.Constant;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.contant.FormWorkPrefix;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/calendar")
public class CalendarController {
    @Resource
    private CalendarService calendarService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private SemesterService semesterService;
    @Resource
    private CourseService courseService;


    /**
     * 根据id返回教学日历
     *
     * @param id calendar id
     */
    @RequestMapping("/getCalendarById")
    public String getCalendarById(int id, Model model) {
        Calendar calendar = calendarService.selectOne(id);
        model.addAttribute("calendar", calendar)
                .addAttribute("main", FormWorkPrefix.TEACHER + "/calendarInfo");
        return "index";
    }

    /**
     * 查询教研室下面的所有课程，按学期展示教学日历
     *
     * @param id 教研室id
     * @return officeManager/courseList
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/getCalendarByResearchRoom")
    public String getCalendarByResearchRoom(int id,
                                            String semesterId,
                                            @RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            HttpSession session,
                                            Model model) {
        List<Semester> semesterList = (List<Semester>) session.getAttribute("nextSemester");
        semesterId = semesterId == null ? semesterList.get(0).getId() : semesterId;
        PageInfo<TeacherGroup> teacherGroupPageInfo = calendarService.getCourseGroupByResearch(id, semesterId, pageNum, pageSize);
        model.addAttribute("data", teacherGroupPageInfo)
                .addAttribute("researchRoomId", id)
                .addAttribute("semesterId", semesterId)
                .addAttribute("semesterName", TimeUtil.getSemesterName(semesterId))
                .addAttribute("main", FormWorkPrefix.OFFICE_MANAGER + "/courseList");
        return "index";
    }

    /**
     * 查询课程组的教学日历用于审核
     *
     * @param courseId 课程id
     * @return officeManager/courseList
     */
    @RequestMapping("/getCourseGroup")
    public String getCourseGroup(String courseId,
                                 String semesterId,
                                 @RequestParam(defaultValue = "1") int pageNum,
                                 @RequestParam(defaultValue = "10") int pageSize,
                                 Model model) {
        PageInfo<Calendar> teacherGroupPageInfo = calendarService.getVerifyStatus(courseId, semesterId, pageNum, pageSize);
        CourseVo courseVo = courseService.findCourseById(courseId);
        model.addAttribute("data", teacherGroupPageInfo)
                .addAttribute("courseId", courseId)
                .addAttribute("courseName", courseVo.getName())
                .addAttribute("semesterId", semesterId)
                .addAttribute("main", FormWorkPrefix.OFFICE_MANAGER + "/courseGroup");
        return "index";
    }

    /**
     * 教师本人的课程列表，展示教学日历
     *
     * @param semesterId semesterId
     * @return teacher/courseListForCalendar
     */
    @RequestMapping("/getCourseListCalendar")
    public String getCourseListCalendar(String semesterId,
                                        @RequestParam(defaultValue = "1") int pageNum,
                                        @RequestParam(defaultValue = "10") int pageSize,
                                        HttpSession session, Model model) {
        semesterId = semesterId == null ? semesterService.getFutureSemester(1, 10).getList().get(0).getId() : semesterId;
        User user = (User) session.getAttribute("user");
        String username = user.getUsername();
        PageInfo<TeacherGroup> teacherGroupPageInfo = teacherService.getTeacherGroupOnTeacher(username, semesterId, pageNum, pageSize);
        model.addAttribute("data", teacherGroupPageInfo)
                .addAttribute("semesterId", semesterId)
                .addAttribute("main", FormWorkPrefix.TEACHER + "/courseListForCalendar");
        return "index";
    }


    /**
     * 教学日历新增完全交给前端做，
     * 接收前端构建的calendar json参数
     *
     * @param jsonObject 不能直接接收对象，teachingContent会自动反序列化
     */
    @RequestMapping(value = "/insert", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    @ResponseBody
    public void insert(@RequestBody JSONObject jsonObject) {
        String teachingContent = jsonObject.getJSONArray("teachingContent").toJSONString();
        Calendar calendar = jsonObject.toJavaObject(Calendar.class);
        calendar.setTeachingContent(teachingContent);
        calendarService.insert(calendar);
    }

    /**
     * @param id courseId
     * @return teacher/calendarInsertInfo
     */
    @RequestMapping("/toUpdate")
    public String toInsert(Integer id, Model model) {
        Calendar calendar = calendarService.selectOne(id);
        model.addAttribute("calendar", calendar)
                .addAttribute("main", FormWorkPrefix.TEACHER + "/calendarUpdateInfo");
        return "index";
    }

    /**
     * @param jsonObject 不能直接接收对象，teachingContent会自动反序列化
     */
    @RequestMapping("/update")
    @ResponseBody
    public void update(@RequestBody JSONObject jsonObject) {
        String teachingContent = jsonObject.getJSONArray("teachingContent").toJSONString();
        Calendar calendar = jsonObject.toJavaObject(Calendar.class);
        calendar.setTeachingContent(teachingContent);
        calendarService.update(calendar);
    }


    /**
     * 教学日历导出接口
     *
     * @param id calendarId
     */
    @RequestMapping("/export/{id}")
    @ResponseBody
    public void export(@PathVariable int id, HttpServletResponse response) throws IOException {
        XWPFTemplate template = calendarService.export(id);
        CalendarData calendarData = Constant.threadLocal.get();
        Constant.threadLocal.remove();
        String fileName = calendarData.getCourseName() + "教学日历-" + calendarData.getTeaName() + "-" + calendarData.getSemesterId() + ".docx";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        response.setContentType("application/msword;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Param", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        OutputStream out = response.getOutputStream();
        template.write(out);
        out.flush();
        out.close();
        template.close();
    }

    /**
     * 教学日历审核接口
     *
     * @param calendarIdList 批量审核，传入id list
     */
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public void verify(@RequestBody List<Integer> calendarIdList, HttpSession session) {
        User user = (User) session.getAttribute("user");
        calendarService.verify(calendarIdList, user.getCurrentRole());
    }

    /**
     * 教学日历复制接口
     */
    @Role({1})
    @RequestMapping(value = "/copy")
    @ResponseBody
    public void copy(@RequestBody JSONObject jsonObject, HttpSession session) {
        User user = (User) session.getAttribute("user");
        calendarService.copy(jsonObject.getInteger("id"), user.getUsername(), jsonObject.getString("courseId"));
    }
}
