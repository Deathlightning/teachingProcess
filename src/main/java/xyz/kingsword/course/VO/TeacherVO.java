package xyz.kingsword.course.VO;

import lombok.Data;

@Data
public class TeacherVO {

    private String id;

    private String name;

    private Integer reasearchId;

    private String phone;

    private String email;

    private String departmentSchool;

    private String education;

    private String gender;

    private String role;

    private String courseInCharge;

    private String specialtyInCharge;
}
