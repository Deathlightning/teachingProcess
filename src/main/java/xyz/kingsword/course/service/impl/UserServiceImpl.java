package xyz.kingsword.course.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.StudentMapper;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.dao.UserMapper;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.UserService;
import xyz.kingsword.course.util.ConditionUtil;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private TeacherMapper teacherMapper;

    /**
     * 通过用户名进行登录
     *
     * @return
     */
    @Override
    public User login(User user) {
        user = userMapper.login(user.getUsername(), user.getPassword());
        Optional.ofNullable(user).orElseThrow(AuthException::new);
//        取第一个角色设置为当前角色
        user.setCurrentRole(Integer.parseInt(user.getRole()));
        return user;
    }


    @Override
    public int resetPassword(String password, User user) {
        if (user.getCurrentRole() == 3) {
            return userMapper.resetPasswordStudent(user.getUsername(), password);
        }
        return userMapper.resetPasswordTeacher(user.getUsername(), password);
    }

    @Override
    public Student getStudentInfo(String userId) {
        Student student = studentMapper.getById(userId);
        Optional.ofNullable(student).orElseThrow(DataException::new);
        return student;
    }

    @Override
    public Teacher getTeacherInfo(String userId) {
        Teacher teacher = teacherMapper.getById(userId);
        Optional.ofNullable(teacher).orElseThrow(DataException::new);
        return teacher;
    }
}
