package xyz.kingsword.course.pojo;

import lombok.Data;
import lombok.ToString;

@Data
public class Student {
    private String id;

    private String password;

    private String name;

    private int classId;

    private String gender;

    private String role;

}