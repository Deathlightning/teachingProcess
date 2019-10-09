package xyz.kingsword.course.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private BookMapper bookMapper;

    @Override
    public void insert(Course course) {
        int flag = courseMapper.insert(course);
        Validator.validateTrue(flag == 1, "课程插入错误，请检查表单内容");
    }

    @Override
    public int deleteById(String id) {
        return courseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public Course findCourseById(String id) {
        return courseMapper.findCourseById(id).orElseThrow(DataException::new);
    }

    @Override
    public int deleteCourse(List<String> list) {
        return courseMapper.deleteCourse(list);
    }

    @Override
    public List<Course> getAllCourseByIdList(List<String> list) {
        return courseMapper.getAllCourseByIdList(list);
    }

    @Override
    public int updateById(Course course) {
        return courseMapper.updateByPrimaryKey(course);
    }

    @Override
    public int setTeacherInCharge(String id, String teaId) {
        return courseMapper.setTeacherInCharge(id, teaId);
    }


    @Override
    public PageInfo<Course> getAllCourse(Integer pageNumber, Integer pageSize) {
        PageInfo<Course> pageInfo = PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> courseMapper.selectAll());
        getCoursePageInfo(pageInfo.getList());
        return pageInfo;
    }

    @Override
    public PageInfo<Course> findCourse(String name, Integer researchRoomId, Integer pageNumber, Integer pageSize) {
        PageInfo<Course> pageInfo = PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> courseMapper.findCourse(name, researchRoomId));
        getCoursePageInfo(pageInfo.getList());
        return pageInfo;
    }

    @Override
    public PageInfo<Course> getCourseOnSemester(String courseName, String semesterId, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> courseMapper.getCourseOnSemester(courseName, semesterId));
    }

    @Override
    public List<Course> getCourseByClassName(String className, String semesterId) {
        return null;
    }

    @Override
    public PageInfo<TeacherGroup> getCourseByTeacher(String teacherId, String semesterId, int pageNum, int pageSize) {
        PageInfo<TeacherGroup> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> courseMapper.getCourseByTeacher(teacherId, semesterId));
        List<TeacherGroup> list = pageInfo.getList();
        list.forEach(v -> v.setSemesterName(TimeUtil.getSemesterName(v.getSemesterId())));
        pageInfo.setList(list);
        System.out.println(pageInfo.getList().size());
        return pageInfo;
    }

    @Override
    public List<Book> getBookByCourse(String courseId) {
        Course course = courseMapper.selectByPrimaryKey(courseId);
        String bookListJson = course.getTextBook();
        List<Book> bookList = new ArrayList<>();
        if (bookListJson.length() > 2)
            bookList = bookMapper.selectBookList(JSON.parseArray(bookListJson, Integer.class));
        return bookList;
    }

    @Override
    public Course checkCourseInCharge(String courseId, String teaId) {
        Course course = findCourseById(courseId);
        Validator.validateTrue(ObjectUtil.equal(course.getTeacherInCharge(), teaId), "您不是该课程管理员", courseId, teaId);
        return course;
    }

    @Override
    public List<Course> getCourseByInCharge(String teaId) {
        return courseMapper.getCourseByInCharge(teaId);
    }

    private void getCoursePageInfo(List<Course> list) {
        list.forEach(e -> {
            System.out.println(e);
            List<Integer> bookIdList = JSONArray.parseArray(e.getTextBook()).toJavaList(Integer.class);
            List<Integer> referenceIdList = JSONArray.parseArray(e.getReferenceBook()).toJavaList(Integer.class);
            List<Book> bookList = bookIdList.isEmpty() ? new ArrayList<>() : bookMapper.selectBookList(bookIdList);
            List<Book> referenceBookList = referenceIdList.isEmpty() ? new ArrayList<>() : bookMapper.selectBookList(referenceIdList);
            e.setBookList(bookList);
            e.setReferenceBookList(referenceBookList);
        });
    }
}