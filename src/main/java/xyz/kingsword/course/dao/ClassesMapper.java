package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Course;

import java.util.List;


public interface ClassesMapper {
    int insert(Classes record);

    int insertList(List<Classes> classesList);

    Classes selectByPrimaryKey(Integer id);

    List<Classes> selectAll();

    List<Classes> findByName(List<String> nameList);

    int updateByPrimaryKey(Classes record);

    /**
     * 获取在校的班级列表
     *
     * @param maxYear 年级最大值 eg:2019 2018 2017 2016
     */
    List<String> selectSchoolClass(int maxYear);

    List<Course> getCurriculum(String className, String semesterId);
}