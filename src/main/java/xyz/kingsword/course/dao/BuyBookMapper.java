package xyz.kingsword.course.dao;


import org.apache.ibatis.annotations.Param;
import xyz.kingsword.course.pojo.BuyBook;

import java.util.List;

public interface BuyBookMapper {
    int deleteByPrimaryKey(Integer id);

    int deleteByIdList(List<Integer> idList);

    int insert(BuyBook record);

    BuyBook selectByPrimaryKey(Integer id);

    List<BuyBook> selectAll();

    List<BuyBook> findHistoryRecordByStuIdAndSemesterId(@Param("stuId") String stuId, @Param("semesterId") String semesterId);

    List<BuyBook> getRecordByStuId(String stuId);

    int subscribeBook(List<BuyBook> list);

    int updateByPrimaryKey(BuyBook record);

    int deleteByBookId(@Param("stuId") String stuId, @Param("bookId") Integer bookId);
}