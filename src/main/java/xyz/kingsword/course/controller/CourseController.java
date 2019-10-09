package xyz.kingsword.course.controller;

import cn.hutool.core.lang.Validator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.util.ResultVOUtil;
import xyz.kingsword.course.util.contant.FormWorkPrefix;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
public class CourseController {

    @Resource
    private CourseService courseService;

    @RequestMapping("courseInfo")
    public String courseInfo(@RequestParam(defaultValue = "") String name,
                             @RequestParam(defaultValue = "-1") Integer researchRoomId,
                             @RequestParam(defaultValue = "1") Integer pageNumber,
                             @RequestParam(defaultValue = "15") Integer pageSize, Model model) {
        PageInfo<Course> pageInfo = courseService.findCourse(name, researchRoomId, pageNumber, pageSize);
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("keyword", name);
        model.addAttribute("researchRoomId", researchRoomId);
        return "admin/course";
    }


    @PostMapping("addCourse")
    public String addCourse(Course course) {
        courseService.updateById(course);
        return "redirect:courseInfo";
    }

    @RequestMapping("setTeacherInCharge")
    @ResponseBody
    public ResultVO setTeacher(@RequestParam String teaId, @RequestParam String id) {
        courseService.setTeacherInCharge(id, teaId);
        return ResultVOUtil.success();
    }

    @PostMapping("deleteCourse")
    @ResponseBody
    public ResultVO deleteCourse(@RequestBody String data) {
        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray array = jsonObject.getJSONArray("idList");
        List<String> list = array.toJavaList(String.class);
        courseService.deleteCourse(list);
        return ResultVOUtil.success();
    }


    @RequestMapping(value = "/courseUpdate", method = RequestMethod.POST)
    public String update(Course course) {
        courseService.updateById(course);
        return "redirect:/managerCourseList";
    }

    @RequestMapping(value = "/toCourseInsert", method = RequestMethod.GET)
    public String toInsert(Model model) {
        model.addAttribute("main", FormWorkPrefix.COURSE_MANAGER + "/courseInsert");
        return "index";
    }

    @RequestMapping(value = "/courseInsert", method = RequestMethod.POST)
    public String insert(Course course) {
        courseService.insert(course);
        return "redirect:/managerCourseList";
    }

    /**
     * 老师按学期查询自己的课程
     */
    @RequestMapping("/teacherCourseList")
    public String courseList(String semesterId,
                             @RequestParam(defaultValue = "1") int pageNum,
                             @RequestParam(defaultValue = "10") int pageSize,
                             HttpSession session, Model model) {
        Semester semester = (Semester) session.getAttribute("nowSemester");
        semesterId = semesterId == null ? semester.getId() : semesterId;
        User user = (User) session.getAttribute("user");
        String teaId = user.getUsername();
        PageInfo<TeacherGroup> teacherGroupPageInfo = courseService.getCourseByTeacher(teaId, semesterId, pageNum, pageSize);
        model.addAttribute("main", FormWorkPrefix.TEACHER + "/courseList")
                .addAttribute("semesterId", semesterId)
                .addAttribute("data", teacherGroupPageInfo);
        return "index";
    }

    @GetMapping("/getCourseById")
    public String getCourseById(String courseId, Model model) {
        Course course = courseService.findCourseById(courseId);
        model.addAttribute("main", FormWorkPrefix.TEACHER + "/courseInfo")
                .addAttribute("course", course);
        return "index";
    }

    /**
     * 课程管理员获取管理的课程列表
     */
    @RequestMapping("/managerCourseList")
    public String managerCourseList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        List<Course> courseList = courseService.getCourseByInCharge(user.getUsername());
        model.addAttribute("data", courseList)
                .addAttribute("main", FormWorkPrefix.COURSE_MANAGER + "/courseList");
        return "index";
    }

    /**
     * 课程管理员获取管理的课程列表
     */
    @RequestMapping("/managerCourseInfo")
    public String managerCourseInfo(HttpSession session, @NonNull String courseId, Model model) {
        User user = (User) session.getAttribute("user");
        Validator.validateNotNull(user, "未登录");
        String teacherId = user.getUsername();
        Course course = courseService.checkCourseInCharge(courseId, teacherId);
        model.addAttribute("course", course)
                .addAttribute("main", FormWorkPrefix.COURSE_MANAGER + "/courseInfo");
        return "index";
    }
}
