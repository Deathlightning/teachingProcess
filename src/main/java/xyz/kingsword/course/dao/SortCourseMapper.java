package xyz.kingsword.course.dao;

import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;

import java.util.List;

public interface SortCourseMapper {
    int deleteSortCourseRecord(List<Integer> idList);

    int insert(SortCourse record);

    int insertList(List<SortCourse> sortCourseList);

    SortCourse selectByPrimaryKey(Integer id);

    int restoreCourseHead(List<Integer> idList);


    int setTeacher(@Param("id") Integer id, @Param("teaId") String teaId);


    /**
     * 多条件查询
     *
     * @param param param {@link xyz.kingsword.course.pojo.param.sortCourse.SearchParam}
     * @return list
     */
    List<SortCourse> search(SearchParam param);

    List<SortCourse> getById(List<Integer> idList);
}