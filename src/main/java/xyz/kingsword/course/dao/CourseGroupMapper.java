package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.CourseGroup;

import java.util.List;

/**
 * 对应视图course_group_view 可查询每一学期的各课程组
 */
public interface CourseGroupMapper {
    /**
     * 多条件查询，不需要可传空
     */
    List<CourseGroup> select(@Param("sortId") Integer sortId, @Param("semesterId") String semesterId, @Param("courseId") String courseId, @Param("teaId") String teaId);
}
