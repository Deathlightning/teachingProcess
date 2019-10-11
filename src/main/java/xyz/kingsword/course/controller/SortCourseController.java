package xyz.kingsword.course.controller;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Validator;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.pojo.param.sortCourse.UpdateParam;
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.service.SortCourseService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@Api("排课操作类")
public class SortCourseController {
    @Autowired
    private SortCourseService sortCourseService;
    @Resource(name = "SortServiceImpl")
    private ExcelService<SortCourse> excelService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ApiOperation(value = "排课列表搜索接口")
    @ApiImplicitParam(name = "searchParam", value = "排课数据查询参数，字段可任意组合，sortCourseFlag为排课标志,已分配为1，未分配-1，全部为0", required = true, dataType = "SearchParam")
    public Result<PageInfo> search(@RequestBody SearchParam searchParam) {
        PageInfo pageInfo = sortCourseService.search(searchParam);
        return new Result<>(pageInfo);
    }

    @RequestMapping(value = "/courseHistory", method = RequestMethod.GET)
    @ApiOperation(value = "课程教授记录")
    @ApiImplicitParam(name = "courseId", value = "课程id", paramType = "query", required = true, dataType = "String")
    public Result courseHistory(String courseId) {
        List<SortCourseVo> sortCourseVoList = sortCourseService.getCourseHistory(courseId);
        return new Result<>(sortCourseVoList);
    }

    @RequestMapping(value = "/teacherHistory", method = RequestMethod.GET)
    @ApiOperation(value = "课程教授记录")
    @ApiImplicitParam(name = "teacherId", value = "教师id", paramType = "query", required = true, dataType = "String")
    public Result teacherHistory(String teacherId) {
        List<SortCourseVo> sortCourseVoList = sortCourseService.getTeacherHistory(teacherId);
        return new Result<>(sortCourseVoList);
    }


    @Role({0, 4})
    @ApiOperation(value = "批量删除排课数据")
    @RequestMapping(value = "deleteSortCourseById", method = RequestMethod.PUT)
    public Result deleteCourseInfo(@RequestBody List<Integer> id) {
        sortCourseService.deleteSortCourseRecord(id);
        return new Result<>();
    }


    @RequestMapping(value = "/setSortCourse", method = RequestMethod.PUT)
    @ApiOperation(value = "给课程设置老师，或者给老师设置课程")
    @ApiImplicitParam(name = "param", value = "id为排课数据id必传，其他任给一个", required = true, dataType = "UpdateParam")
    public Result setSortCourse(@RequestBody UpdateParam param) {
        sortCourseService.setSortCourse(param);
        return new Result();
    }

    @RequestMapping(value = "/sortCourseImport", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "排课数据导入")
    public Result sortCourseImport(MultipartFile file) throws IOException {
        Optional.ofNullable(file).orElseThrow(() -> new ValidateException("文件上传错误"));
        ConditionUtil.validateTrue(!file.isEmpty()).orElseThrow(() -> new ValidateException("文件上传错误"));
        String semesterId = file.getOriginalFilename().substring(0, 5);
        List<SortCourse> sortCourseList = excelService.excelImport(file.getInputStream());
        sortCourseList.forEach(v -> v.setSemesterId(semesterId));
        sortCourseService.insertSortCourseList(sortCourseList);
        return new Result();
    }

    @RequestMapping(value = "/mergeCourseHead", method = RequestMethod.PUT)
    @ApiOperation(value = "课头合并")
    public Result merge(@RequestBody List<Integer> idList) {
        sortCourseService.mergeCourseHead(idList);
        return new Result();
    }

    @RequestMapping(value = "/restoreCourseHead", method = RequestMethod.PUT)
    @ApiOperation(value = "课头重置")
    public Result restoreCourseHead(@RequestBody List<Integer> idList) {
        sortCourseService.restoreCourseHead(idList);
        return new Result();
    }

    /**
     * 教学任务导出
     */
    @RequestMapping(value = "/sortCourseExport", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "教学任务导出")
    public void export(HttpServletResponse response, @RequestBody String semesterId) throws IOException {
        Workbook workbook = sortCourseService.excelExport(semesterId);
        String fileName = TimeUtil.getSemesterName(semesterId) + "教学任务.xls";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Param", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        workbook.write(response.getOutputStream());

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    private void semesterValidate(String fileName) {
        try {
            String semesterId = TimeUtil.getSemesterName(fileName);
            Validator.validateTrue(semesterId.length() != 6, "排课表上传文件名错误");
        } catch (Exception e) {
            log.error("排课表上传文件名错误");
            throw new ValidateException("排课表上传文件名错误");
        }
    }
}
