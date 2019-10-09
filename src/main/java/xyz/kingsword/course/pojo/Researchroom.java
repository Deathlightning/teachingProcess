package xyz.kingsword.course.pojo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class Researchroom {
    private Integer id;

    private String name;

    private String teacherInCharge;

    private List<Teacher> teachers;
}