package xyz.kingsword.course.pojo;

import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class Semester {
    private String id;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private Integer status;

}