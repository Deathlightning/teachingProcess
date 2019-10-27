package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.pojo.param.CourseSelectParam;

import java.util.List;
import java.util.Optional;

public interface CourseMapper {
    int deleteByPrimaryKey(String id);

    int deleteCourse(List<String> list);

    Optional<Course> findCourseById(String id);

    List<Course> getByIdList(List<String> idList);

    int insert(Course record);

    Course selectByPrimaryKey(String id);

    List<Course> findCourse(@Param("name") String name, @Param("researchRoomId") Integer researchRoomId);

    List<Course> selectAll();

    int updateByPrimaryKey(Course record);

    int setTeacherInCharge(@Param("courseId") String courseId, @Param("teaId") String teaId);

    List<Course> getAllCourseByIdList(List<String> list);

    /**
     * @param courseName 课程名称 可为空
     * @param semesterId 学期id
     * @return 课程号 课程名 课程性质 教材id
     */
    List<Course> getCourseOnSemester(@Param("courseName") String courseName, @Param("semesterId") String semesterId);

    List<Course> getCourseBySemester(String semesterId);

    List<TeacherGroup> getCourseByTeacher(@Param("teacherId") String teacherId, @Param("semesterId") String semesterId);

    void addCourseBook(@Param("bookId") int bookId, @Param("courseId") String courseId);

    List<TeacherGroup> selectTeacherGroup(@Param("semesterId") String semesterId, @Param("courseId") String courseId);

    List<Course> getCourseByInCharge(String teaId);

    List<Course> select(CourseSelectParam param);


}