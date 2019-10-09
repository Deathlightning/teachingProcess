package xyz.kingsword.course.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.StudentVo;
import xyz.kingsword.course.VO.TeacherVo;
import xyz.kingsword.course.dao.StudentMapper;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.dao.UserMapper;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Student;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.service.UserService;

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
     */
    @Override
    public User login(User user) {
        user = userMapper.login(user.getUsername(), SecureUtil.md5(user.getUsername() + user.getPassword()));
        Optional.ofNullable(user).orElseThrow(() -> new AuthException("账号或密码不正确"));
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
    public Object getUserInfo(User user) {
        if (isStudent(user)) {
            StudentVo studentVo = new StudentVo();
            Student student = studentMapper.getById(user.getUsername());
            Optional.ofNullable(student).orElseThrow(DataException::new);
            BeanUtils.copyProperties(student, studentVo);
            return studentVo;
        }
        TeacherVo teacherVo = new TeacherVo();
        Teacher teacher = teacherMapper.getById(user.getUsername());
        Optional.ofNullable(teacher).orElseThrow(DataException::new);
        BeanUtils.copyProperties(teacher, teacherVo);
        teacherVo.setCurrentRole(user.getCurrentRole());
        return teacherVo;
    }

    private boolean isStudent(User user) {
        List<Integer> roleList = JSON.parseArray(user.getRole()).toJavaList(Integer.class);
        return roleList.get(0) == RoleEnum.STUDENT.getCode();
    }
}
