package xyz.kingsword.course.pojo;


import lombok.Data;

import java.io.Serializable;

/**
 * @author wzh
 */
@Data
public class Book implements Serializable {
    private Integer id;
    private String isbn;

    private String name;

    private String author;

    private String publish;

    private String price;

    private String note;

    private String teaId;

    /**
     * -2删除-1审核不通过0正常
     */
    private Integer status;

    private String pubDate;

    private String award;

    private String edition;

    /**
     * 书籍图片url
     */
    private String imgUrl;

    /**
     * 为老师留几本书
     */
    private Integer forTeacher;

    private static final long serialVersionUID = 1L;

}