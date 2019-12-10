package xyz.kingsword.course.service;

import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.VO.CourseGroupOrderVo;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.param.DeclareBookExportParam;

import java.util.Collection;
import java.util.List;

public interface BookOrderService {
    List<Integer> insert(List<BookOrder> bookOrderList);

    void forTeacherIncrease(Collection<Integer> bookIdList);

    void cancelPurchase(int id);

    List<BookOrderVo> select(String studentId, String semesterId, String className);

    List<BookOrderVo> selectByTeacher(String teacherId, String semesterId);

    List<CourseGroupOrderVo> courseGroupOrder(String courseId, String semesterId);

    Workbook exportAllStudentRecord(DeclareBookExportParam param);

    Workbook exportSingleRecord(String studentId);

    Workbook exportClassRecord(String className, String semesterId);
}
