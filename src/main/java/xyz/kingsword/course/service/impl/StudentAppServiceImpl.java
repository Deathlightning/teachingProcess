package xyz.kingsword.course.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.BuyBookMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.dao.SortCourseMapper;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.BuyBook;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.service.StudentAppService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentAppServiceImpl implements StudentAppService {
    @Autowired
    private BuyBookMapper buyBookMapper;

    @Autowired
    private BookMapper bookMapper;

    @Autowired
    private BookService bookService;

    @Autowired
    private SortCourseMapper sortcourseMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public List<SortCourse> getSortCourseByClassNameAndSemesterId(String className, String semesterId) {
//        List<SortCourse> list = sortcourseMapper.getSortCourseByClassNameAndSemesterId(new SearchParam().set);
//        List<Course> courseList = courseMapper.getCourseOnSemester(null, semesterId);
//        courseList.forEach(e -> {
//            List<Integer> bookIdList = JSONArray.parseArray(e.getTextBook())
//                    .toJavaList(Integer.class);
//            List<Integer> referenceIdList = JSONArray.parseArray(e.getReferenceBook())
//                    .toJavaList(Integer.class);
//            e.setBookList(bookService.selectBookList(bookIdList));
//            e.setReferenceBookList(bookService.selectBookList(referenceIdList));
//        });
        return new ArrayList<>();
    }



    @Override
    public int subscribeBook(List<BuyBook> buyBookList) {
        return buyBookMapper.subscribeBook(buyBookList);
    }

    @Override
    public List<BuyBook> findHistoryRecordByStuIdAndSemesterId(String stuId, String semesterId) {
        return buyBookMapper.findHistoryRecordByStuIdAndSemesterId(stuId, semesterId);
    }

    @Override
    public List<BuyBook> getAllHistoryRecord(String stuId) {
        return buyBookMapper.getRecordByStuId(stuId);
    }

    @Override
    public int deleteSubscribeBook(String stuId, Integer bookId) {
        return buyBookMapper.deleteByBookId(stuId, bookId);
    }

}
