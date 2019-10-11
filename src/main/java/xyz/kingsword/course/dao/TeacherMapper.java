package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.TeacherGroup;

import java.util.List;
import java.util.Optional;

public interface TeacherMapper {
    int deleteByPrimaryKey(String teaId);

    int insert(Teacher record);

    List<Teacher> selectAll();

    List<Teacher> getByIdList(List<String> idList);

    List<Teacher> getAllTeacherByRole(@Param("roleId") String id);

    List<Teacher> findTeacherByName(@Param("name") String name);

    int updateByPrimaryKey(Teacher record);

    int updateResearch(@Param("teaId") String teaId, @Param("researchId") String researchId);

    List<TeacherGroup> getTeacherGroup(String courseId, String semesterId, int pageNum, int pageSize);

    List<TeacherGroup> getTeacherGroupOnTeacher(String teaId, String semesterId, int pageNum, int pageSize);

    List<Teacher> getAllTeacherByCourseName(String name);

    int countTeacherGroup(String courseId, String semesterId);

    Teacher getById(String id);

    Optional<Teacher> getByName(String Name);
}