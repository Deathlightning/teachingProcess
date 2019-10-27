package xyz.kingsword.course.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.pojo.param.TrainingProgramSearchParam;
import xyz.kingsword.course.service.TrainingProgramService;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/trainingProgram")
@Api(tags = "培养方案控制类")
public class TrainingProgramController {
    @Resource
    private TrainingProgramService trainingProgramService;

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public Result importData(MultipartFile file) throws IOException {
        trainingProgramService.importData(file.getInputStream());
        return new Result();
    }

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Result insert(@RequestBody TrainingProgram trainingProgram) {
        trainingProgramService.insert(trainingProgram);
        return new Result();
    }


    @RequestMapping(value = "/select", method = RequestMethod.POST)
    public Result select(@RequestBody TrainingProgramSearchParam param) {
        PageInfo<TrainingProgram> pageInfo = trainingProgramService.select(param);
        return new Result<>(pageInfo);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public Result update(@RequestBody TrainingProgram trainingProgram) {
        trainingProgramService.update(trainingProgram);
        return new Result();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result update(int id) {
        trainingProgramService.delete(Collections.singletonList(id));
        return new Result();
    }
}
