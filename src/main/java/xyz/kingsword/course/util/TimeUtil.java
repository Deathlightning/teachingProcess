package xyz.kingsword.course.util;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xyz.kingsword.course.dao.SemesterMapper;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

/**
 * 与学期，学年有关的日期util
 *
 * @author wzh
 **/
@Component
public class TimeUtil {

    @Autowired
    private SemesterMapper semesterMapper;
    private static TimeUtil timeUtil;

    /**
     * 在静态方法里调用spring注入的方法
     */
    @PostConstruct
    public void init() {
        timeUtil = this;
    }

    /**
     * 获取下一学期的semester
     *
     * @return eg:18191
     */
    public static String getNextSemesterId() {
        int semesterIndex = getOtherSemesterIndex();
        return composeSemesterPrefix(semesterIndex);
    }

    /**
     * 获取当前学期的semester
     *
     * @return eg:18191
     */
    public static String getNowSemesterId() {
        int semesterIndex = getNowSemesterIndex();
        return composeSemesterPrefix(semesterIndex);
    }

    /**
     * 根据学期id获取学期名字
     *
     * @param semesterIdString eg:19201
     * @return 2019-2020学年第一学期
     */
    public static String getSemesterName(String semesterIdString) {
        String semesterName;
        try {
            int semesterId = Integer.parseInt(semesterIdString);
            Validator.validateFalse(semesterId <= 10000, "学期参数过大");
            String semester = semesterId % 2 == 1 ? "一" : "二";
            semesterId = semesterId / 10;
            int rear = semesterId % 100;
            int font = semesterId / 100;
            semesterName = "20" + font + "-20" + rear + "学年第" + semester + "学期";
        } catch (Exception e) {
            e.printStackTrace();
            throw new ValidateException("学期id异常：" + semesterIdString);
        }

        return semesterName;
    }

    /**
     * 通过year name获取yearId<br>
     *
     * @param name eg:2019-2020学年1学期
     * @return yearId 19201
     * @since 1.1.0
     */
    @Deprecated
    public static int getSemesterId(String name) {
        String prefix = name.substring(2, 4);
        String suffix = name.substring(5, 7);
        String semesterIndex = String.valueOf(name.charAt(11));
        return Integer.parseInt(prefix + suffix + semesterIndex);
    }

    /**
     * 获取当前学期
     *
     * @return 1 or 2
     */
    private static int getNowSemesterIndex() {
        int month = LocalDate.now().getMonthValue();
        return month >= 2 && month <= 7 ? 2 : 1;
    }

    /**
     * 获取上一学期或下一学期,根据当前学期，另一个学期不是1就是2
     *
     * @return 1 or 2
     */
    private static int getOtherSemesterIndex() {
        int nowSemesterIndex = getNowSemesterIndex();
        return nowSemesterIndex == 1 ? 2 : 1;
    }


    /**
     * 获取过去五年的年级
     *
     * @return [14, 15, 16, 17,18]
     */
    public static int[] getHistoryYear() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int value1 = year - 1;
        int value2 = year - 2;
        int value3 = year - 3;
        int value4 = year - 4;
        int value5 = year - 5;
        return month > 7 ? new int[]{value5, value4, value3, value2, value1, year} : new int[]{value5, value4, value3, value2, value1};
    }

    /**
     * 获取在校生的年级
     *
     * @param semesterIndex 1 or 2
     * @return eg:16 17 18 19
     */
    public static int[] getStudents(int semesterIndex) {
        int year = LocalDate.now().getYear() % 100;
        return semesterIndex == 1 ? new int[]{year, year - 1, year - 2, year - 3} : new int[]{year - 1, year - 2, year - 3, year - 4};
    }

    /**
     * 组装学期前缀eg:1920
     *
     * @param semesterIndex 1 or 2
     * @return eg:19201
     */
    private static String composeSemesterPrefix(int semesterIndex) {
        LocalDate now = LocalDate.now();
        StringBuilder stringBuilder = new StringBuilder();
        if (semesterIndex == 1) {
            stringBuilder.append(now.getYear() % 100).append(now.getYear() % 100 + 1).append(semesterIndex);
            return stringBuilder.toString();
        }
        stringBuilder.append(now.getYear() % 100 - 1).append(now.getYear() % 100).append(semesterIndex);
        return stringBuilder.toString();
    }
}
