package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.Classes;

import java.util.List;


public interface ClassesMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Classes record);

    Classes selectByPrimaryKey(Integer id);

    List<Classes> selectByIdList(List<Integer> idList);

    List<Classes> selectAll();

    List<Classes> findByName(List<String> nameList);

    int updateByPrimaryKey(Classes record);

    /**
     * 获取在校的班级列表
     *
     * @param maxYear 年级最大值 eg:2019 2018 2017 2016
     * @return
     */
    List<String> selectSchoolClass(int maxYear);
}