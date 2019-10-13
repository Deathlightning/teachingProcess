package xyz.kingsword.course.service;


import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Classes;

import java.util.List;

public interface ClassesService {

    void insert(List<Classes> classesList);

    void updateById(Classes Classes);

    PageInfo<Classes> getAllClasses(Integer pageNumber, Integer pageSize);

    List<Classes> getByName(List<String> name);

    List<Classes> getSchoolClass();
}
