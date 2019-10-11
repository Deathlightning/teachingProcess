package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Mapper;
import xyz.kingsword.course.pojo.Semester;

import java.util.List;

@Mapper
public interface SemesterMapper {
    int insert(Semester record);


    int updateById(Semester semester);

    List<Semester> findByName(String name);

    List<Semester> selectAll();

    /**
     * 获取当前和未来学期
     */
    List<Semester> getFutureSemester();

}