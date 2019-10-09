package xyz.kingsword.course.VO;

import lombok.Data;

@Data
public class StudentVo {

    private String id;

    private String name;

    private int classId;

    private String gender;

    private String role;

    private final int currentRole = 3;

    public StudentVo() {
    }

}
