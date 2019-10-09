package xyz.kingsword.course.service;

import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.User;

public interface UserService {
    User login(User user);

    int resetPassword(String newPassword, User user);

    Student getStudentInfo(String userId);

    Teacher getTeacherInfo(String userId);
}
