package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.Student;

import java.util.List;


public interface StudentMapper {
    int insert(Student record);

    int deleteById(String id);

    List<Student> findByName(String name);

    List<Student> selectAll();

    int updateStudent(Student student);

    Student getById(String id);

}