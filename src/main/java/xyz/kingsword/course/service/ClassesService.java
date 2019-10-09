package xyz.kingsword.course.service;


import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Classes;

public interface ClassesService {

    int addClasses(Classes Classes);

    int deleteById(Integer id);

    int updateById(Classes Classes);

    PageInfo<Classes> getAllClasses(Integer pageNumber, Integer pageSize);

    PageInfo<Classes> findByName(String name ,Integer pageNumber,Integer pageSize);
}
