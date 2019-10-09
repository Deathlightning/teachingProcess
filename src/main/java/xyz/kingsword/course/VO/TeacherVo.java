package xyz.kingsword.course.VO;

import lombok.Data;

@Data
public class TeacherVo {
    private String id;

    private String name;

    private Integer researchId;

    private String phone;

    private String email;

    private String departmentSchool;

    private String teachingTitle;

    private String education;

    private String gender;

    private String role;

    private int currentRole;

    /**
     * 管理的课程id，存json
     */
    private String courseInCharge;

    /**
     * 管理的专业id，存json
     */
    private String specialtyInCharge;
}
