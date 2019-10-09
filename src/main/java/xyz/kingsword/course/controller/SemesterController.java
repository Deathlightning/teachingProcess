package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;

import java.util.List;

/**
 * 学期起始时间在八月和九月间变动，无法确定，需要由管理员进行学期起始时间设置
 */
@Api("学期相关类")
@RestController
public class SemesterController {

    @Autowired
    private SemesterService semesterService;


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增学期")
    public Result addSemester(Semester semester) {
        semesterService.addSemester(semester);
        return new Result<>();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("仅修改开始时间结束时间")
    public Result updateSemester(@RequestBody Semester Semester) {
        semesterService.updateById(Semester);
        return new Result();
    }

    @RequestMapping("/getAll")
    @ApiOperation("获取全部学期")
    public Result getAllSemester() {
        PageInfo<Semester> list = semesterService.getAllSemester(1, 10);
        return new Result<>(list);
    }

    @GetMapping("/getFuture")
    @ApiOperation("获取当前以及未来学期")
    public Result getFutureSemester() {
        PageInfo<Semester> list = semesterService.getFutureSemester(1, 10);
        return new Result<>(list);
    }
}
