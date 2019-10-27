package xyz.kingsword.course.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.service.ClassesService;

import java.util.List;

@RestController
@RequestMapping("/classes")
public class ClassesController {

    @Autowired
    private ClassesService classesService;


    @PostMapping("/insert")
    @ApiOperation("新增")
    public String addClasses(List<Classes> classesList) {
        classesService.insert(classesList);
        return "redirect:classesInfo";
    }

    @PostMapping("/update")
    @ApiOperation("新增修改")
    public String updateStudent(Classes Classes) {
        classesService.updateById(Classes);
        return "redirect:classesInfo";
    }

    @GetMapping("/getCurriculum")
    @ApiOperation("获取课程表")
    public Result getCurriculum(String className, String semesterId) {
        List<Course> courseList = classesService.getCurriculum(className, semesterId);
        return new Result<>(courseList);
    }
}
