package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Semester;

public interface SemesterService {

    void addSemester(Semester semester);

    int updateById(Semester semester);

    PageInfo<Semester> getAllSemester(Integer pageNumber, Integer pageSize);

    PageInfo<Semester> findByName(String name, Integer pageNumber, Integer pageSize);


    /**
     * 获取当前和未来所有学期
     */
    PageInfo<Semester> getFutureSemester(Integer pageNumber, Integer pageSize);
}