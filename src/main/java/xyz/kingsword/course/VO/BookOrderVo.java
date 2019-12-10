package xyz.kingsword.course.VO;

import lombok.Data;

@Data
public class BookOrderVo {
    private String userId;
    /**
     * 用户的名字
     */
    private String userName;

    private int orderId;

    private int bookId;
    /**
     * 书名
     */
    private String name;

    private double price;

    private String semesterId;

    private String courseId;

    private String className;
}
