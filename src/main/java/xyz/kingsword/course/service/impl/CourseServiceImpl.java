package xyz.kingsword.course.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.VO.CourseVo;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.enmu.AssessmentEnum;
import xyz.kingsword.course.enmu.CourseTypeEnum;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.pojo.param.CourseSelectParam;
import xyz.kingsword.course.service.CourseService;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private BookMapper bookMapper;

    @Override
    @Transactional
    public void insert(Course course) {
        int flag = courseMapper.insert(course);
        log.debug("课程插入，{}", flag);
    }

    @Override
    public int deleteById(String id) {
        return courseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Course> select(CourseSelectParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> courseMapper.select(param));
    }

    @Override
    public CourseVo findCourseById(String id) {
        Course course = courseMapper.findCourseById(id).orElseThrow(() -> new DataException(ErrorEnum.DATA_ERROR));
        return renderCourseVo(course);
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
    public void setTeacherInCharge(String id, String teaId) {
        courseMapper.setTeacherInCharge(id, teaId);
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
    public Course checkCourseInCharge(String courseId, String teaId) {
        return null;
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

    private CourseVo renderCourseVo(Course course) {
        List<Integer> textBookIdList = JSONArray.parseArray(course.getTextBook(), Integer.class);
        List<Integer> referenceBookIdList = JSONArray.parseArray(course.getReferenceBook(), Integer.class);
        if (textBookIdList.isEmpty())
            textBookIdList.add(-1);
        if (referenceBookIdList.isEmpty())
            referenceBookIdList.add(-1);
        List<Book> textBookList = bookMapper.selectBookList(textBookIdList);
        List<Book> referenceBookList = bookMapper.selectBookList(referenceBookIdList);
        CourseVo courseVo = new CourseVo();

        BeanUtils.copyProperties(course, courseVo);
        courseVo.setType(CourseTypeEnum.get(course.getType()).getContent());
        courseVo.setAssessmentWay(AssessmentEnum.getContent(course.getAssessmentWay()).getContent());
        courseVo.setBookList(textBookList);
        courseVo.setReferenceBookList(referenceBookList);
        return courseVo;
    }
}