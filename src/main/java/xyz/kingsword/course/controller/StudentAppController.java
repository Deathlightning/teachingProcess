package xyz.kingsword.course.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.pojo.BuyBook;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.service.StudentAppService;
import xyz.kingsword.course.util.Constant;
import xyz.kingsword.course.util.ResultVOUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentAppController {
    @Autowired
    private StudentAppService studentAppService;


    @RequestMapping("/getHistoryRecord")
    public String getHistoryRecord(HttpSession session, Model model) {
        Object o = session.getAttribute(Constant.SESSION_STUDENT_USERINFO);
        Student student = (Student) o;
        List<BuyBook> list = studentAppService.getAllHistoryRecord(student.getId());
        model.addAttribute("list", list);
        return "student/record";
    }

    @RequestMapping("/subscribeBook")
    @ResponseBody
    public ResultVO subscribeBook(@RequestBody String data, HttpSession session) {
        Object o = session.getAttribute(Constant.SESSION_STUDENT_USERINFO);
        Student student = (Student) o;
        List<BuyBook> buyBookList = JSONObject.parseObject(data)
                .getJSONArray("buyBookList")
                .toJavaList(BuyBook.class);
        System.out.println("buyBookList = " + buyBookList);
        buyBookList.forEach(e -> {
            e.setStuId(student.getId());
            e.setSemesterId(TimeUtil.getNextSemesterId());
        });
        studentAppService.subscribeBook(buyBookList);
        return ResultVOUtil.success();
    }


    @RequestMapping("/deleteSubscribeBookByBookId")
    @ResponseBody
    public ResultVO updateSubscribeBook(@RequestBody String data,
                                        HttpSession session) {
        Object o = session.getAttribute(Constant.SESSION_STUDENT_USERINFO);
        Student student = (Student) o;
        Integer bookId = JSONObject.parseObject(data).getInteger("bookId");
        studentAppService.deleteSubscribeBook(student.getId(), bookId);
        return ResultVOUtil.success();
    }

    @GetMapping("/userInfo")
    public String userInfo() {
        return "student/personal";
    }

}
