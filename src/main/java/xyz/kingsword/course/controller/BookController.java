package xyz.kingsword.course.controller;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.service.TeacherService;
import xyz.kingsword.course.util.BookUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.contant.FormWorkPrefix;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {
    @Resource
    private BookService bookService;
    @Resource
    private CourseService courseService;
    @Resource
    private TeacherService teacherService;

    /**
     * 按课程查看教材列表
     *
     * @param courseId 课程id
     */
    @RequestMapping(value = "/selectBookByCourse")
    public String selectBookByCourse(String courseId, String semesterId, Model model) {
        List<Book> bookList = courseService.getBookByCourse(courseId);
        Book book=new Book();
        int count = teacherService.countTeacherGroup(courseId, semesterId);
        model.addAttribute("textBookList", bookList)
                .addAttribute("course", courseService.findCourseById(courseId))
                .addAttribute("forTeacher", count)
                .addAttribute("main", FormWorkPrefix.TEACHER + "/textBookList");
        return "index";
    }

    /**
     * 查看单个教材的详情
     *
     * @param id 教材id
     */
    @RequestMapping(value = "/bookInfo", method = RequestMethod.GET)
    public String bookInfo(int id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book)
                .addAttribute("main", FormWorkPrefix.ACADEMIC_MANAGER + "/bookInfo");
        return "index";
    }

    /**
     * 更新给老师留几本教材
     *
     * @param num 新的数量
     * @param id  教材id
     */
    @RequestMapping(value = "/updateForTeacher", method = RequestMethod.POST)
    public String updateForTeacher(int num, int id) {
        bookService.updateForTeacher(num, id);
        return "index";
    }


    /**
     * 新增教材
     */
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public String insert(Book book, String courseId) {
        System.out.println(book);
        bookService.insert(book, courseId);
        return "redirect:" + "/book/selectBookByCourse?courseId=" + courseId;
    }

    @RequestMapping(value = "/queryBook", method = RequestMethod.GET)
    public String queryBook(String ISBN, String courseId, Model model) {
        List<TeacherGroup> teacherList = teacherService.getTeacherGroup(courseId, TimeUtil.getFutureSemester().get(0).getId(), 1, 10).getList();
        Book book = BookUtil.getBook(ISBN);
        model.addAttribute("courseId", courseId)
                .addAttribute("book", book)
                .addAttribute("forTeacher", teacherList.size())
                .addAttribute("main", FormWorkPrefix.TEACHER + "/fullInBookInfo");
        return "index";
    }

    @RequestMapping(value = "/queryBookName", method = RequestMethod.GET)
    @ResponseBody
    public Object queryBookName(String ISBN) {
        ResultVO<String> resultVO = new ResultVO<>();
        resultVO.setData(BookUtil.getBookName(ISBN));
        return resultVO;
    }


    @RequestMapping(value = "/toUpdateBook", method = RequestMethod.GET)
    public String toUpdateBook(int id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book)
                .addAttribute("main", FormWorkPrefix.TEACHER + "/updateBookInfo");
        return "index";
    }

    @RequestMapping(value = "/updateBook", method = RequestMethod.POST)
    @ResponseBody
    public void updateBook(@RequestBody Book book) {
        bookService.updateBook(book);
    }

    /**
     * 教材订购信息导出
     *
     * @param semesterId 学期id
     */
    @RequestMapping(value = "/exportBookInfo", method = RequestMethod.GET)
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
