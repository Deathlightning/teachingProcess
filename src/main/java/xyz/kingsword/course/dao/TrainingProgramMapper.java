package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.TrainingProgram;

import java.util.List;

@Mapper
public interface TrainingProgramMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(TrainingProgram record);

    int insertList(List<TrainingProgram> recordList);

    int insertSelective(TrainingProgram record);


    TrainingProgram selectByPrimaryKey(Integer id);

    TrainingProgram selectByCourseIdGrade(@Param("courseId") String courseId, @Param("grade") int grade);


    int updateByPrimaryKeySelective(TrainingProgram record);

    int updateByPrimaryKey(TrainingProgram record);


    List<TrainingProgram> select(@Param("grade") Integer grade, @Param("courseName") String courseName);

    List<Integer> getGrades();

    List<TrainingProgram> getByCourseId(List<String> courseList);
}