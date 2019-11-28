package xyz.kingsword.course.controller;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.annocations.Role;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Api(tags = "学生订书记录")
@RequestMapping("/bookOrder")
@RestController
public class BookOrderController {
    @Autowired
    private BookOrderService bookOrderService;


    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增订书记录")
    public Result insert(@RequestBody List<BookOrder> bookOrderList) {
        User user = UserUtil.getUser();
        bookOrderList.parallelStream().forEach(v -> v.setUserId(user.getUsername()));
        List<Integer> idList = bookOrderService.insert(bookOrderList);
        return new Result<>(idList);
    }

    @RequestMapping(value = "/cancelPurchase", method = RequestMethod.GET)
    @ApiOperation("取消订教材")
    public Result cancelPurchase(int id) {
        bookOrderService.cancelPurchase(id);
        return new Result();
    }

    @RequestMapping(value = "/getStudentOrder", method = RequestMethod.GET)
    @ApiOperation("获取学生订书记录")
    @Role({RoleEnum.STUDENT})
    public Result getStudentOrder(String semesterId) {
        List<BookOrderVo> bookOrderVoList = bookOrderService.select(UserUtil.getUser().getUsername(), semesterId, null);
        BigDecimal sum = bookOrderVoList.parallelStream().map(v -> BigDecimal.valueOf(v.getPrice())).reduce(BigDecimal::add).orElse(new BigDecimal(0));
        Dict dict = Dict.create()
                .set("bookList", bookOrderVoList)
                .set("sum", sum.toString());
        return new Result<>(dict);
    }

    /**
     * 班级教材订购信息导出
     *
     * @param semesterId 学期id
     */
    @RequestMapping(value = "/exportClassBookInfo", method = RequestMethod.GET)
    @ApiOperation("班级教材订购信息导出")
    public void exportClassBookInfo(HttpServletResponse response, String className, String semesterId) throws IOException {
        Workbook workbook = bookOrderService.exportClassRecord(className, semesterId);
        String fileName = className + "-" + TimeUtil.getSemesterName(semesterId) + "教材征订计划表.xlsx";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Param", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        workbook.write(response.getOutputStream());
    }


    /**
     * 全部教材订购信息导出
     *
     * @param semesterId 学期id
     */
    @RequestMapping(value = "/exportBookInfo", method = RequestMethod.GET)
    @ApiOperation("教材订购信息导出")
    public void exportBookInfo(HttpServletResponse response, String semesterId) throws IOException {
        Workbook workbook = bookOrderService.exportAllStudentRecord(semesterId);
        String fileName = TimeUtil.getSemesterName(semesterId) + "教材征订计划表.xlsx";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Param", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        workbook.write(response.getOutputStream());
    }
}
