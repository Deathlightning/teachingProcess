package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.TeacherGroup;

import java.util.List;

public interface TeacherService {

    int addTeacher(Teacher teacher);

    int addTeacher(List<Teacher> list);

    int deleteTeacher(String teaId);

    int updateTeacher(Teacher teacher);

    PageInfo<Teacher> findTeacherByName(String name, Integer pageNumber, Integer pageSize);

    PageInfo<Teacher> getAllTeachers(Integer pageNumber, Integer pageSize);

    List<Teacher> getAllPersonInCharge();

    int setResearch(String teaId, String researchId);

    /**
     * 根据课程选取课程组
     * 不同学期课程组不同
     *
     * @param courseId   课程id
     * @param semesterId 学期id
     * @return TeacherGroup
     */
    PageInfo<TeacherGroup> getTeacherGroup(String courseId, String semesterId, int pageNum, int pageSize);


    PageInfo<TeacherGroup> getTeacherGroupOnTeacher(String teaId, String semesterId, int pageNum, int pageSize);

    int countTeacherGroup(String courseId, String semesterId);

    List<Integer> getCourseList(String teacherId, String semesterId);

    /**
     * 通过id查询一个老师的信息
     *
     * @param id
     * @return teacher
     */
    Teacher getById(String id);
}
