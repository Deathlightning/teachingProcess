package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.param.ClassesSelectParam;
import xyz.kingsword.course.service.ClassesService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/classes")
@Api(tags = "班级接口")
public class ClassesController {

    @Autowired
    private ClassesService classesService;


    @PostMapping("/insert")
    @ApiOperation("新增")
    public Result insert(List<Classes> classesList) {
        classesService.insert(classesList);
        return new Result();
    }

    @PostMapping("/update")
    @ApiOperation("修改")
    public Result update(Classes Classes) {
        classesService.update(Classes);
        return new Result<>();
    }

    @PostMapping("/select")
    @ApiOperation("查")
    public Result select(@RequestBody ClassesSelectParam param) {
        PageInfo<Classes> pageInfo = classesService.select(param);
        return new Result<>(pageInfo);
    }

    @GetMapping("/selectGrades")
    @ApiOperation("获取在校年级")
    public Result<List<Integer>> selectGrades() {
        return new Result<>(Arrays.asList(2016, 2017, 2018, 2019));
    }
}
