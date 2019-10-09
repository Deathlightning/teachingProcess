package xyz.kingsword.course.pojo;

public class Classes {
    private Integer id;

    private String classname;

    private Integer studentNum;

    private int admissionTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname == null ? null : classname.trim();
    }

    public Integer getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(Integer studentNum) {
        this.studentNum = studentNum;
    }

    public int getAdmissionTime() {
        return admissionTime;
    }

    public void setAdmissionTime(int admissionTime) {
        this.admissionTime = admissionTime;
    }
}