package xyz.kingsword.course.pojo;

import lombok.Data;

@Data
public class Student {
    private String id;

    private String password;

    private String name;

    private String className;

    private String gender;

    private final int roleId = 3;

}