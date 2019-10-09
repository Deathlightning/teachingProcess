package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;

import java.util.List;

public interface SortCourseService {

    void insertSortCourse(SortCourse sortCourse);

    void insertSortCourseList(List<SortCourse> sortCourseList);

    void setTeacher(Integer id, String teaId);


    void deleteSortCourseRecord(List<Integer> id);

    PageInfo<SortCourse> search(SearchParam param);


    PageInfo<SortCourse> searchOnName(String couName);

    List<SortCourse> getTeacherList(String teaId);


    void mergeCourseHead(List<Integer> id);

    void restoreCourseHead(List<Integer> id);


    Workbook excelExport(String semesterId);
}
