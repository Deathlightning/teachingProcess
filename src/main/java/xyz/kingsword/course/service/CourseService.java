package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.VO.CourseVo;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.pojo.param.CourseSelectParam;

import java.util.List;

public interface CourseService {

    void insert(Course course);

    int deleteById(String id);

    CourseVo findCourseById(String id);

    int deleteCourse(List<String> list);

    List<Course> getAllCourseByIdList(List<String> list);

    int updateById(Course course);

    void setTeacherInCharge(String id, String teaId);

    PageInfo<Course> getAllCourse(Integer pageNumber, Integer pageSize);

    PageInfo<Course> findCourse(String name, Integer researchRoomId, Integer pageNumber, Integer pageSize);


    List<Course> getCourseByClassName(String className, String semesterId);

    PageInfo<TeacherGroup> getCourseByTeacher(String teacherId, String semesterId, int pageNum, int pageSize);

    Course checkCourseInCharge(String courseId, String teaId);

    List<Course> getCourseByInCharge(String teaId);

    PageInfo<Course> select(CourseSelectParam param);

//    List<Course> selectCourseOnTeacher(String teaId, String semester, int pageNum, int pageSize);
}
