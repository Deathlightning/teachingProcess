package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.service.TrainingProgramService;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.contant.FormWorkPrefix;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(value = "教材相关类")
@RestController
@RequestMapping("/academic")
public class AcademicManagerController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private TrainingProgramService trainingProgramService;

    /**
     * 教学部查看整体订书情况
     */
    @RequestMapping(value = "/textBookDeclareList", method = RequestMethod.POST)
    @ApiOperation(value = "书籍订阅情况查看")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户id", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true, paramType = "query", dataType = "String"),
    })
    public Result textBookDeclareList(String semesterId,
                                      String courseName,
                                      Integer pageNum,
                                      Integer pageSize,
                                      HttpSession session) {
        Semester semester = (Semester) session.getAttribute("nowSemester");
        semesterId = semesterId == null ? semester.getId() : semesterId;
        PageInfo<Course> pageInfo = courseService.getCourseOnSemester(courseName, semesterId, pageNum, pageSize);
        return new Result<>(pageInfo);
    }


    /**
     * 查看培养方案
     *
     * @param grade      对应届别 eg：2017级 2018级
     * @param courseName 课程名称
     */
    @Role({0})
    @RequestMapping("/trainingProgramIndex")
    public String trainingProgramIndex(Integer grade, String courseName,
                                       @RequestParam(defaultValue = "1") int pageNum,
                                       @RequestParam(defaultValue = "10") int pageSize,
                                       Model model
    ) {
        PageInfo<TrainingProgram> pageInfo = trainingProgramService.select(grade, courseName, pageNum, pageSize);
        List<Integer> gradeList = trainingProgramService.getGrades();
        model.addAttribute("data", pageInfo)
                .addAttribute("gradeList", gradeList)
                .addAttribute("courseName", courseName)
                .addAttribute("grade", grade)
                .addAttribute("main", FormWorkPrefix.ACADEMIC_MANAGER + "/trainingProgramIndex");
        return "index";
    }

    @RequestMapping("/toUpdate")
    public String toUpdate(int id, Model model) {
        TrainingProgram trainingProgram = trainingProgramService.selectById(id);
        model.addAttribute("trainingProgram", trainingProgram)
                .addAttribute("main", FormWorkPrefix.ACADEMIC_MANAGER + "/trainingProgramUpdate");
        return "index";
    }

    @RequestMapping("/trainingProgramUpdate")
    public String update(TrainingProgram trainingProgram) {
        trainingProgramService.update(trainingProgram);
        return "redirect:/academic/trainingProgramIndex";
    }

    @RequestMapping(value = "/uploadTrainingProgram", method = RequestMethod.POST)
    @ResponseBody
    public Object uploadTrainingProgram(@RequestParam("file") MultipartFile file) throws IOException {
        System.out.println(file);
        System.out.println(file.getContentType());
        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        InputStream inputStream = file.getInputStream();
        trainingProgramService.importExcel(inputStream);
        return new ResultVO<>();
    }

    @RequestMapping("/test")
    public String test(Model model) {
        model.addAttribute("main", "test");
        return "index";
    }
}
