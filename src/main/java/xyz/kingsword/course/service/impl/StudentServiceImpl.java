package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.dao.SortCourseMapper;
import xyz.kingsword.course.dao.StudentMapper;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.service.StudentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private SortCourseMapper sortCourseMapper;

    @Override
    public int addStudent(Student student) {
        return studentMapper.insert(student);
    }

    @Override
    public int deleteById(String id) {
        return studentMapper.deleteById(id);
    }

    @Override
    public int updateById(Student student) {
        return studentMapper.updateStudent(student);
    }

    @Override
    public Student getById(String id) {
        return studentMapper.getById(id);
    }

    @Override
    public PageInfo<Student> getAllStudent(Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> studentMapper.selectAll());
    }

    @Override
    public PageInfo<Student> findByName(String name, Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> studentMapper.findByName(name));
    }

    /**
     * 获取班级课程表
     */
    @Override
    public List<SortCourse> getCurriculum(SearchParam param) {
//        List<SortCourseVo> sortCourseList = sortCourseMapper.search(param);
//        return sortCourseList.parallelStream().filter(v -> v.getFlag() == 1).collect(Collectors.toList());
        return null;
    }
}
