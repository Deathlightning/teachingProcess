package xyz.kingsword.course.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.TeacherVo;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.pojo.Teacher;
import xyz.kingsword.course.pojo.TeacherGroup;
import xyz.kingsword.course.service.TeacherService;
import xyz.kingsword.course.util.Constant;
import xyz.kingsword.course.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private CourseMapper courseMapper;

    @Override
    public int addTeacher(Teacher teacher) {
        teacher.setPassword(SecureUtil.md5(Constant.DEFAULT_PASSWORD));
        return teacherMapper.insert(teacher);
    }

    @Override
    public int addTeacher(List<Teacher> list) {
        return 0;
    }

    @Override
    public int deleteTeacher(String teaId) {
        return teacherMapper.deleteByPrimaryKey(teaId);
    }

    @Override
    public int updateTeacher(Teacher teacher) {
        return teacherMapper.updateByPrimaryKey(teacher);
    }

    @Override
    public PageInfo<Teacher> findTeacherByName(String name, Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize)
                .doSelectPageInfo(() -> teacherMapper.findTeacherByName(name));
    }

    @Override
    public PageInfo<Teacher> getAllTeachers(Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize)
                .doSelectPageInfo(() -> teacherMapper.selectAll());
    }

    @Override
    public List<Teacher> getAllPersonInCharge() {
        String roleId = "4"; //专业负责人id
        return teacherMapper.getAllTeacherByRole(roleId);
    }

    @Override
    public int setResearch(String teaId, String researchId) {
        return teacherMapper.updateResearch(teaId, researchId);
    }

    /**
     * 分页查询课程组全部信息
     *
     * @param courseId   课程id
     * @param semesterId 学期id
     * @return TeacherGroup
     */
    @Override
    public PageInfo<TeacherGroup> getTeacherGroup(String courseId, String semesterId, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> teacherMapper.getTeacherGroup(courseId, semesterId, pageNum, pageSize));
    }

    /**
     * 老师根据学期查自己的课程组
     *
     * @param teaId      teacherId
     * @param semesterId 学期id
     * @return TeacherGroup
     */
    @Override
    public PageInfo<TeacherGroup> getTeacherGroupOnTeacher(String teaId, String semesterId, int pageNum, int pageSize) {
        PageInfo<TeacherGroup> pageInfo = PageHelper.startPage(pageNum, pageSize)
                .doSelectPageInfo(() -> teacherMapper.getTeacherGroupOnTeacher(teaId, semesterId, pageNum, pageSize));
        List<TeacherGroup> teacherGroupList = pageInfo.getList();
        teacherGroupList.forEach(v -> v.setSemesterName(TimeUtil.getSemesterName(v.getSemesterId())));
        pageInfo.setList(teacherGroupList);
        return pageInfo;
    }

    @Override
    public int countTeacherGroup(String courseId, String semesterId) {
        return courseMapper.selectTeacherGroup(semesterId, courseId).size();
    }

    /**
     * 根据教师id，学期id获取课程list
     *
     * @param teacherId  teacherId
     * @param semesterId semesterId
     * @return 课程id list
     */
    @Override
    public List<Integer> getCourseList(String teacherId, String semesterId) {
        return null;
    }

    @Override
    public Teacher getById(String id) {
        return teacherMapper.getById(id);
    }
}
