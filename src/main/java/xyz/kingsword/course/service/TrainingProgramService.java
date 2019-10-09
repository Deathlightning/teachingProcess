package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.TrainingProgram;

import java.io.InputStream;
import java.util.List;


public interface TrainingProgramService {
    void insert(TrainingProgram record);

    void insert(List<TrainingProgram> record);

    void update(TrainingProgram record);

    /**
     * @return list
     */
    PageInfo<TrainingProgram> select(Integer grade, String courseName, Integer pageNum, Integer pageSize);

    TrainingProgram selectById(int id);

    void importExcel(InputStream inputStream);

    /**
     * 列举级别 eg：2017 2018 2019
     */
    List<Integer> getGrades();

    List<TrainingProgram> getByCourseId(List<String> courseIdList);
}
