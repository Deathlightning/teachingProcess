package xyz.kingsword.course.controller;

import cn.hutool.core.lang.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.Result;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.BookUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/book")
@Api(tags = "教材相关接口")
public class BookController {
    @Resource
    private BookService bookService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ApiOperation("新增")
    public Result insert(@RequestBody Book book, String courseId) {
        bookService.insert(book, courseId);
        return new Result();
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    @ApiOperation("更新")
    public void update(@RequestBody Book book) {
        bookService.update(book);
    }

    /**
     * 按课程查看教材列表
     *
     * @param courseId 课程id
     */
    @RequestMapping(value = "/getTextBook", method = RequestMethod.GET)
    @ApiOperation("按课程查询教材")
    public Result getTextBook(String courseId) {
        List<Book> bookList = bookService.getTextBook(courseId);
        return new Result<>(bookList);
    }

    /**
     * 按课程查看参考书列表
     *
     * @param courseId 课程id
     */
    @RequestMapping(value = "/getReferenceBook", method = RequestMethod.GET)
    @ApiOperation("按课程查询参考书")
    public Result selectReferenceBookByCourse(String courseId) {
        List<Book> bookList = bookService.getReferenceBook(courseId);
        return new Result<>(bookList);
    }

    /**
     * 查看单个教材的详情
     *
     * @param id 教材id
     */
    @RequestMapping(value = "/bookInfo", method = RequestMethod.GET)
    @ApiOperation("查看单个教材的详情")
    public Result bookInfo(int id) {
        Book book = bookService.getBook(id);
        return new Result<>(book);
    }

    /**
     * 更新给老师留几本教材
     *
     * @param num 新的数量
     * @param id  教材id
     */
    @RequestMapping(value = "/updateForTeacher", method = RequestMethod.POST)
    @ApiOperation("更新给老师留几本教材")
    public Result updateForTeacher(int num, int id) {
        bookService.updateForTeacher(num, id);
        return new Result<>();
    }


    @RequestMapping(value = "/isbn", method = RequestMethod.GET)
    @ApiOperation("教材查询接口，同步远程接口信息")
    public Result queryBook(String ISBN) {
        Book book = BookUtil.getBook(ISBN);
        return new Result<>(book);
    }

    @RequestMapping(value = "/purchase", method = RequestMethod.POST)
    @ApiOperation("学生订书接口")
    public Result purchase(@RequestBody List<BookOrder> bookOrderList) {
        bookService.purchase(bookOrderList);
        return new Result();
    }

    @RequestMapping(value = "/getStudentOrder", method = RequestMethod.POST)
    @ApiOperation("获取学生订书记录")
    public Result getStudentOrder(String semesterId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String username = user.getUsername();
        List<Book> bookList = bookService.getBookOrder(username, semesterId);
        BigDecimal sum = bookList.parallelStream().map(v -> BigDecimal.valueOf(v.getPrice())).reduce(BigDecimal::add).orElse(new BigDecimal(0));
        Dict dict = Dict.create()
                .set("bookList", bookList)
                .set("sum", sum.toString());
        return new Result<>(dict);
    }


    /**
     * 教材订购信息导出
     *
     * @param semesterId 学期id
     */
    @RequestMapping(value = "/exportBookInfo", method = RequestMethod.GET)
    @ApiOperation("教材订购信息导出")
    public void exportBookInfo(HttpServletResponse response, String semesterId) throws IOException {
        Workbook workbook = bookService.exportBookSubscription(semesterId);
        String fileName = TimeUtil.getSemesterName(semesterId) + "教材征订计划表.xlsx";
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
        response.setContentType("application/msexcel;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.addHeader("Param", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        workbook.write(response.getOutputStream());
    }

}
