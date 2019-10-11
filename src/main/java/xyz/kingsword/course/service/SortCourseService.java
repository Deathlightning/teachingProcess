package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.pojo.param.sortCourse.UpdateParam;

import java.util.List;

public interface SortCourseService {

    void insertSortCourse(SortCourse sortCourse);

    void insertSortCourseList(List<SortCourse> sortCourseList);

    void setTeacher(Integer id, String teaId);

    void setSortCourse(UpdateParam updateParam);

    void deleteSortCourseRecord(List<Integer> id);

    List<SortCourseVo> getCourseHistory(String courseId);

    List<SortCourseVo> getTeacherHistory(String courseId);

    PageInfo<SortCourseVo> search(SearchParam param);


    List<SortCourse> getTeacherList(String teaId);


    void mergeCourseHead(List<Integer> id);

    void restoreCourseHead(List<Integer> id);


    Workbook excelExport(String semesterId);
}
