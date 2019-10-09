package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.pojo.ExecutePlan;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.service.ExecuteService;
import xyz.kingsword.course.service.TrainingProgramService;
import xyz.kingsword.course.util.ResultVOUtil;
import xyz.kingsword.course.util.contant.FormWorkPrefix;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行计划控制类
 */
@Controller
@RequestMapping("/executePlan")
public class ExecutePlanController {
    @Resource
    private TrainingProgramService trainingProgramService;
    @Resource
    private ExecuteService executeService;
    @Resource(name = "ExecuteServiceImpl")
    private ExcelService<ExecutePlan> excelService;

    /**
     * 通过上传执行计划检测与培养方案的不同
     * errorList为存在问题的执行计划的课程id+课程名
     */
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    @ResponseBody
    public Object verify(MultipartFile file) throws IOException {
        List<ExecutePlan> executePlanList = excelService.excelImport(file.getInputStream());
        final int grade = executePlanList.get(0).getGrade();
        List<String> courseIdList = executePlanList.stream().map(ExecutePlan::getCourseId).collect(Collectors.toList());
        List<TrainingProgram> trainingProgramList = trainingProgramService.getByCourseId(courseIdList).stream().filter(v -> v.getGrade() == grade).collect(Collectors.toList());
        List<String> errorList = executeService.verify(executePlanList, trainingProgramList);
        return ResultVOUtil.success(errorList);
    }

    /**
     * 查看培养方案
     *
     * @param grade      对应届别 eg：2017级 2018级
     * @param courseName 课程名称
     */
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
                .addAttribute("main", FormWorkPrefix.SPECIALTY + "/trainingProgramIndex");
        return "index";
    }
}
