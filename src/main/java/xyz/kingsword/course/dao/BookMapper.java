package xyz.kingsword.course.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.CourseBook;

import java.util.List;

@Mapper
public interface BookMapper {
    int insert(Book record);

    int update(Book record);

    int insertSelective(Book record);

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
}