package xyz.kingsword.course.controller;


import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import xyz.kingsword.course.VO.CourseVo;
import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.service.TeacherService;
import xyz.kingsword.course.util.ResultVOUtil;
import xyz.kingsword.course.util.contant.FormWorkPrefix;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/teacher")
@Api(tags = "教师管理")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private CourseService courseService;


    @RequestMapping("findTeacherByName")
    public String findTeacherByName(@RequestParam(defaultValue = "") String name,
                                    @RequestParam(defaultValue = "1") Integer pageNumber,
                                    @RequestParam(defaultValue = "15") Integer pageSize, Model model) {
        PageInfo<Teacher> teacherPageInfo = teacherService.findTeacherByName(name, pageNumber, pageSize);
        model.addAttribute("pageInfo", teacherPageInfo);
        model.addAttribute("keyword", name);
        return "admin/teacherInfo";
    }

    @RequestMapping("deleteTeacher")
    public RedirectView deleteTeacher(@RequestParam String id,
                                      @RequestParam(defaultValue = "1") Integer pageNumber,
                                      @RequestParam(defaultValue = "15") Integer pageSize) {
        RedirectView redirectView = new RedirectView();
        teacherService.deleteTeacher(id);
        redirectView.setUrl("teacherInfo");
        redirectView.addStaticAttribute("pageNumber", pageNumber);
        redirectView.addStaticAttribute("pageSize", pageSize);
        return redirectView;
    }

    @PostMapping("addTeacher")
    public String addTeacher(Teacher teacher) {
        teacherService.addTeacher(teacher);
        return "admin/teacherInfo";
    }

    @PostMapping("/updateTeacher")
    public String updateTeacher(Teacher teacher) {
        teacherService.updateTeacher(teacher);
        return "redirect:teacherInfo";
    }

    @GetMapping("/toAddCalendar")
    public String toAddCalendar(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return "admin/teacherInfo";
    }

    @RequestMapping("/setResearchRoom")
    @ResponseBody
    public ResultVO setResearchRoom(@RequestParam String id, @RequestParam String researchroomId) {
        teacherService.setResearch(id, researchroomId);
        return ResultVOUtil.success();
    }

    @RequestMapping("/teacherPersonInCharge")
    public String teacher(Model model) {
        List<Teacher> list = teacherService.getAllPersonInCharge();
        model.addAttribute("list", list);
        return "admin/personInCharge";
    }

    @RequestMapping(value = "getAllTeacher", method = RequestMethod.GET)
    @ApiOperation("查询全部老师信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码，从1开始", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "全部可传0", required = true, dataType = "int", paramType = "query")
    })
    public Result getAllTeacher(int pageNum, int pageSize) {
        PageInfo<Teacher> list = teacherService.getAllTeachers(pageNum, pageSize);
        return new Result<>(list);
    }

    /**
     * 普通教师s获取课程组信息
     *
     * @param pageNum    页码
     * @param pageSize   页展示数量
     * @param courseId   课程id
     * @param semesterId 学期id
     * @return teacher/courseGroup
     */
    @RequestMapping("/getCourseGroup")
    public String getCourseGroup(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            String courseId,
            String semesterId,
            Model model) {
        CourseVo courseVo = courseService.findCourseById(courseId);
        PageInfo<TeacherGroup> teacherGroupPageInfo = teacherService.getTeacherGroup(courseId, semesterId, pageNum, pageSize);
        model.addAttribute("data", teacherGroupPageInfo)
                .addAttribute("course", courseVo)
                .addAttribute("semesterId", semesterId)
                .addAttribute("main", FormWorkPrefix.TEACHER + "/courseGroup");
        return "index";
    }
}
