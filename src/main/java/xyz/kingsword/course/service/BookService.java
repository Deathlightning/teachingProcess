package xyz.kingsword.course.service;

import org.apache.poi.ss.usermodel.Workbook;
import xyz.kingsword.course.pojo.Book;

import java.util.List;

public interface BookService {

    Book getBookById(int id);

    Workbook exportBookSubscription(String semesterId);

    void updateForTeacher(int num, int id);

    List<Book> selectBookList(List<Integer> idList);

    void updateBook(Book book);

    void insert(Book book, String courseId);
}
