package xyz.kingsword.course.pojo;

import lombok.Data;
import lombok.ToString;

@Data
public class Teacher {
    private String id;

    private String password;

    private String name;

    private Integer reasearchId;

    private String phone;

    private String email;

    private String departmentSchool;

    private String teachingTitle;

    private String education;

    private String gender;

    private String role;

    /**
     * 管理的课程id，存json
     */
    private String courseInCharge;

    /**
     * 管理的专业id，存json
     */
    private String specialtyInCharge;

}