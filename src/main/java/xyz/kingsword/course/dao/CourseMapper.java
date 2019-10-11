package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import org.openxmlformats.schemas.drawingml.x2006.main.STAdjAngle;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.TeacherGroup;

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

    int setTeacherInCharge(@Param("id") String id, @Param("teaId") String teaId);

    List<Course> getAllCourseByIdList(List<String> list);

    /**
     * @param courseName 课程名称 可为空
     * @param semesterId 学期id
     * @return 课程号 课程名 课程性质 教材id
     */
    List<Course> getCourseOnSemester(@Param("courseName") String courseName, @Param("semesterId") String semesterId);

    List<TeacherGroup> getCourseByTeacher(@Param("teacherId") String teacherId, @Param("semesterId") String semesterId);

    void addCourseBook(@Param("bookId") int bookId, @Param("courseId") String courseId);

    List<TeacherGroup> selectTeacherGroup(@Param("semesterId") String semesterId, @Param("courseId") String courseId);

    List<Course> getCourseByInCharge(String teaId);
}