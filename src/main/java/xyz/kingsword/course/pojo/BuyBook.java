package xyz.kingsword.course.pojo;

import lombok.Data;
import lombok.ToString;

@Data
public class BuyBook {
    private Integer id;

    private String stuId;

    private String courseId;

    private Integer bookId;

    private String semesterId;

    private Book book;
}