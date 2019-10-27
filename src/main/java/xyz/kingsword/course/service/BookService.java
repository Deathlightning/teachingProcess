package xyz.kingsword.course.service;

import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.BookOrder;

import java.util.List;

public interface BookService {

    void insert(Book book, String courseId);

    void delete(int id);

    Book getBook(int id);

    void update(Book book);

    void updateForTeacher(int num, int id);

    List<Book> getTextBook(String courseId);

    List<Book> getReferenceBook(String courseId);

    List<Book> getByIdList(List<Integer> idList);

    List<Book> getBookOrder(String studentId,String semesterId);

    void purchase(List<BookOrder> bookOrderList);

    Workbook exportBookSubscription(String semesterId);

}
