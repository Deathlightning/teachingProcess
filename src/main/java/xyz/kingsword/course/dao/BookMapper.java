package xyz.kingsword.course.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.CourseBook;
import xyz.kingsword.course.pojo.DO.BookExportViewDo;
import xyz.kingsword.course.pojo.DO.BookOrderDo;

import java.util.List;

@Mapper
public interface BookMapper {
    int insert(Book record);

    int update(Book record);

    int delete(int id);


    List<Book> selectBookList(List<Integer> idList);

    int updateForTeacher(@Param("num") int num, @Param("id") int id);

    Book selectBookByPrimaryKey(int id);

    /**
     * 每本书每个班订了多少
     *
     * @param bookId     一门课的教材id 不考虑list 容易出现bug
     * @param semesterId 学期id
     */
    int purchaseRecord(@Param("bookId") Integer bookId, @Param("semesterId") String semesterId);

    List<CourseBook> getCourseBook(String semesterId);

    /**
     * 以班级为单位查询订单
     *
     * @param semesterId
     * @return
     */
    List<BookOrderDo> getClassBookOrder(String semesterId);

    int purchase(List<BookOrder> bookOrderList);

    List<Book> getBookOrder(String studentId, String semesterId);

    /**
     * 导出订书excel专用
     */
    List<BookExportViewDo> getCourseInfo(String semesterId);
}