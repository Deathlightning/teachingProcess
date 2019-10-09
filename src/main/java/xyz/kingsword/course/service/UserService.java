package xyz.kingsword.course.service;

import xyz.kingsword.course.VO.StudentVo;
import xyz.kingsword.course.VO.TeacherVo;
import xyz.kingsword.course.pojo.User;

public interface UserService {
    User login(User user);

    int resetPassword(String newPassword, User user);

    Object getUserInfo(User user);

}
