package xyz.kingsword.course.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.kingsword.course.dao.ClassesMapper;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 本类是对在校班级操作的工具类
 */
@Component
public class ClassNameUtil {

    @Autowired
    private ClassesMapper classesMapper;
    private static ClassNameUtil classNameUtil;

    /**
     * 在静态方法里调用spring注入的方法
     */
    @PostConstruct
    public void init() {
        classNameUtil = this;
    }

    /**
     * 获取在校的班级名称数组
     *
     * @param semesterIndex 1 or 2
     * @return []
     */
    public static List<String> getSchoolClass(int semesterIndex) {
        int year = LocalDate.now().getYear();
        System.out.println(classNameUtil);
        return classNameUtil.classesMapper.selectSchoolClass(semesterIndex == 1 ? year : year - 1).stream().sorted().collect(Collectors.toList());
    }
}
