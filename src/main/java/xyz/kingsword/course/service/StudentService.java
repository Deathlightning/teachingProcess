package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.param.SortCourseSearchParam;

import java.util.List;

public interface StudentService {

    int addStudent(Student student);

    int deleteById(String id);

    int updateById(Student student);

    Student getById(String id);

    PageInfo<Student> getAllStudent(Integer pageNumber, Integer pageSize);

    PageInfo<Student> findByName(String name, Integer pageNumber, Integer pageSize);

    List<SortCourse> getCurriculum(SortCourseSearchParam param);
}
