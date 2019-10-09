package xyz.kingsword.course.service;

import xyz.kingsword.course.pojo.BuyBook;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.Student;

import java.util.List;

public interface StudentAppService {

    List<SortCourse> getSortCourseByClassNameAndSemesterId(String className, String semesterId);



    int subscribeBook(List<BuyBook> buyBookList);

    List<BuyBook> findHistoryRecordByStuIdAndSemesterId(String stuId, String semesterId);

    List<BuyBook> getAllHistoryRecord(String stuId);

    int deleteSubscribeBook(String stuId, Integer bookId);
}
