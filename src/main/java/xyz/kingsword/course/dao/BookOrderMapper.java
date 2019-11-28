package xyz.kingsword.course.dao;

import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.DO.BookExportCourseDo;

import java.util.List;

public interface BookOrderMapper {
    int insert(List<BookOrder> bookOrderList);

    int delete(int id);

    List<BookOrderVo> select(String userId, String semesterId, String className);

    List<BookOrderVo> selectByTeacher(String teacherId, String semesterId);

    List<BookExportCourseDo> export(String semesterId);

    List<String> purchaseClass(String semesterId);
}
