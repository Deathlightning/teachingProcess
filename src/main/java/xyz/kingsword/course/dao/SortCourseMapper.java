package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.pojo.param.sortCourse.UpdateParam;

import java.util.List;

public interface SortCourseMapper {
    int deleteSortCourseRecord(List<Integer> idList);

    int insert(SortCourse record);

    int insertList(List<SortCourse> sortCourseList);

    SortCourse selectByPrimaryKey(Integer id);

    int mergeCourseHead(List<Integer> idList);

    int restoreCourseHead(List<Integer> idList);

    int setTeacher(@Param("id") Integer id, @Param("teaId") String teaId);


    /**
     * 多条件查询
     *
     * @param param param {@link xyz.kingsword.course.pojo.param.sortCourse.SearchParam}
     * @return list
     */
//    List<SortCourseVo> searchVo(SearchParam param);

    List<SortCourseVo> search(SearchParam param);

    List<SortCourse> getById(List<Integer> idList);

    /**
     * 获取排课历史信息
     */
    List<SortCourseVo> getTeacherHistory(@Param("teacherId") String teacherId, @Param("semesterId") String semesterId);

    List<SortCourseVo> getCourseHistory(@Param("courseId") String courseId, @Param("semesterId") String semesterId);

    int setSortCourse(UpdateParam param);
}