package xyz.kingsword.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.dao.SortCourseMapper;
import xyz.kingsword.course.dao.TeacherMapper;
import xyz.kingsword.course.enmu.CourseTypeEnum;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.SortCourse;
import xyz.kingsword.course.pojo.param.sortCourse.SearchParam;
import xyz.kingsword.course.pojo.param.sortCourse.UpdateParam;
import xyz.kingsword.course.service.ClassesService;
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.service.SortCourseService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("SortServiceImpl")
public class SortServiceImpl implements SortCourseService, ExcelService<SortCourse> {

    @Resource
    private SortCourseMapper sortcourseMapper;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private ClassesService classesService;


    @Override
    @Transactional
    public void insertSortCourse(SortCourse sortCourse) {
        sortcourseMapper.insert(sortCourse);
    }

    @Override
    public void insertSortCourseList(List<SortCourse> sortCourseList) {
        sortcourseMapper.insertList(sortCourseList);
    }

    @Override
    @Transactional
    public void setTeacher(Integer id, String teaId) {
        int flag = sortcourseMapper.setTeacher(id, teaId);
        ConditionUtil.validateTrue(flag != 0).orElseThrow(DataException::new);
    }

    @Override
    public void setSortCourse(UpdateParam updateParam) {
        int flag = sortcourseMapper.setSortCourse(updateParam);
        log.debug("排课更新数据：{}", flag);
    }


    @Override
    public void deleteSortCourseRecord(List<Integer> id) {

    }

    @Override
    public List<SortCourseVo> getCourseHistory(String courseId) {
        String nowSemesterId = TimeUtil.getFutureSemester().get(0).getId();
        return sortcourseMapper.getCourseHistory(courseId, nowSemesterId);
    }

    @Override
//    需要优化
    public List<SortCourseVo> getTeacherHistory(String teacherId) {
        String nowSemesterId = TimeUtil.getFutureSemester().get(0).getId();
        return sortcourseMapper.getTeacherHistory(teacherId, nowSemesterId);
    }

    @Override
    public PageInfo<SortCourseVo> search(SearchParam param) {
        return PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> sortcourseMapper.search(param));
    }


    /**
     * 课头合并类，需要合并学生人数，班级id，课头id
     *
     * @param idList 待合并课头id
     */
    @Override
    public void mergeCourseHead(List<Integer> idList) {
        List<SortCourse> sortCourseList = sortcourseMapper.getById(idList);
        mergeCheck(sortCourseList);
        SortCourse mainSortCourse = new SortCourse();
        BeanUtil.copyProperties(sortCourseList.get(0), mainSortCourse);
        List<Integer> mergedIdList = new ArrayList<>(sortCourseList.size());
        String className = sortCourseList.parallelStream().map(SortCourse::getClassName).distinct().collect(Collectors.joining(" "));
        int studentNum = sortCourseList.parallelStream().mapToInt(SortCourse::getStudentNum).sum();
        mainSortCourse.setClassName(className);
        mainSortCourse.setStudentNum(studentNum);
        mainSortCourse.setMergedId(JSON.toJSONString(mergedIdList));

        sortcourseMapper.mergeCourseHead(idList);
        sortcourseMapper.insert(mainSortCourse);
    }

    /**
     * 重置课头，先把合并的课头删除，再把被合并进去的课头状态改为正常显示
     */
    @Override
    public void restoreCourseHead(List<Integer> idList) {
        sortcourseMapper.deleteSortCourseRecord(idList);
        List<SortCourse> sortCourseList = sortcourseMapper.getById(idList);
        List<Integer> mergedIdList = new ArrayList<>(sortCourseList.size() * 2);
        for (SortCourse sortCourse : sortCourseList) {
            mergedIdList.addAll(JSON.parseArray(sortCourse.getMergedId()).toJavaList(Integer.class));
        }
        mergedIdList = mergedIdList.stream().distinct().collect(Collectors.toList());
        sortcourseMapper.restoreCourseHead(mergedIdList);
    }

    /**
     * 合并前检查courseId和teacherId
     */
    private void mergeCheck(List<SortCourse> sortCourseList) {
        ConditionUtil.validateTrue(sortCourseList.size() != 1).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_ERROR));
        Set<String> set = sortCourseList.parallelStream().map(SortCourse::getCouId).collect(Collectors.toSet());
        ConditionUtil.validateTrue(set.size() == 1).orElseThrow(() -> new OperationException(ErrorEnum.DIFFERENT_COURSE));
        set = sortCourseList.parallelStream().map(SortCourse::getTeaId).collect(Collectors.toSet());
        ConditionUtil.validateTrue(set.size() == 1).orElseThrow(() -> new OperationException(ErrorEnum.DIFFERENT_TEACHER));
    }


    /**
     * 获取教过该课程的教师列表
     */
    @Override
    public List<SortCourse> getTeacherList(String teaId) {
        return new ArrayList<>();
    }

    /**
     * 模板21列，从rowIndex=6开始读
     *
     * @param inputStream excelInputStream
     * @return List<SortCourse>
     */
    @Override
    public List<SortCourse> excelImport(InputStream inputStream) {
        Workbook workbook;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Sheet sheet = workbook.getSheetAt(0);
        List<SortCourse> sortCourseList = new ArrayList<>(sheet.getLastRowNum());
        int excelLength = sheet.getLastRowNum() - 2;
        for (int i = 6; i < excelLength; i++) {
            Row row = sheet.getRow(i);
            SortCourse sortCourse = new SortCourse();
            sortCourse.setCouId(row.getCell(2).getStringCellValue());

//            处理className
            String className = row.getCell(4).getStringCellValue().trim();
            String[] classNameArray = ReUtil.delAll("\\([^)]*\\)", className).split(",");

            Validator.validateFalse(classNameArray.length == 0, "第" + (row.getRowNum() + 1) + "行班级名称有误");
            List<Classes> classesList = classesService.getByName(Arrays.asList(classNameArray));
            Validator.validateTrue(classesList.size() == classNameArray.length, "第" + (row.getRowNum() + 1) + "行班级名称有误");
//            final List<Integer> classIdList = classesList.stream().map(Classes::getId).collect(toList());
            className = classesList.parallelStream().map(Classes::getClassname).collect(Collectors.joining(" "));
            sortCourse.setClassName(className);
            sortCourse.setStudentNum(classesList.stream().mapToInt(Classes::getStudentNum).sum());

            String teaId = teacherMapper.getByName(row.getCell(12)
                    .getStringCellValue()).orElseThrow(() -> new DataException("第" + (row.getRowNum() + 1) + "行教师姓名有误")).getId();
            sortCourse.setTeaId(teaId);
            sortCourse.setStatus(0);
            sortCourseList.add(sortCourse);
        }
        log.info("教学任务导入完毕");
        return sortCourseList;
    }

    public Workbook excelExport(String semesterId) {
        InputStream inputStream = SortServiceImpl.class.getClassLoader().getResourceAsStream("templates/sortCourse.xls");
        Workbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Sheet sheet = workbook.getSheetAt(0);
//        setExcelHeader(workbook, semesterId);
        CellStyle cellStyle = getBaseCellStyle(workbook);
        String[][] data = renderExportData(semesterId);
        int startRow = 6;
        Row row = sheet.getRow(2);
        row.setRowStyle(cellStyle);
        row.getCell(0).setCellValue(TimeUtil.getSemesterName(semesterId));
        for (int i = 0; i < data.length; i++) {
            row = sheet.createRow(i + startRow);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data[i][j]);
            }
        }
        renderFoot(workbook);
        return workbook;
    }

    private void renderFoot(Workbook workbook) {
        CellStyle cellStyle = getBaseCellStyle(workbook);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        Sheet sheet = workbook.getSheetAt(0);
        final int lastRow = sheet.getLastRowNum();
        Row row = sheet.createRow(lastRow + 1);
        row.setRowStyle(getBaseCellStyle(workbook));
//        设置内容
        row.createCell(0).setCellValue("学院（部）院长签字：");
        row.createCell(5).setCellValue("系（教研室）主任签字：");
        row.createCell(16).setCellValue("打印日期：");
        row.createCell(18).setCellValue(LocalDate.now().toString());
//          设置样式
        row.getCell(0).setCellStyle(cellStyle);
        row.getCell(5).setCellStyle(cellStyle);
        row.getCell(16).setCellStyle(cellStyle);
        row.getCell(18).setCellStyle(cellStyle);

        sheet.addMergedRegion(new CellRangeAddress(lastRow + 1, lastRow + 2, 0, 3));
        sheet.addMergedRegion(new CellRangeAddress(lastRow + 1, lastRow + 2, 5, 9));
        sheet.addMergedRegion(new CellRangeAddress(lastRow + 1, lastRow + 2, 16, 17));
        sheet.addMergedRegion(new CellRangeAddress(lastRow + 1, lastRow + 2, 18, 19));
    }

    /**
     * 构建导出排课表数据
     * （课序号未导出）
     */
    private String[][] renderExportData(String semesterId) {
        List<SortCourseVo> sortCourseList = sortcourseMapper.search(new SearchParam().setSemesterId(semesterId));
        String[][] data = new String[sortCourseList.size()][19];
        for (int i = 0; i < sortCourseList.size(); i++) {
            SortCourseVo sortCourseVo = sortCourseList.get(i);
            String[] strings = new String[20];
            String courseId = sortCourseVo.getCourseId();
            Course course = courseMapper.selectByPrimaryKey(courseId);
            strings[0] = String.valueOf(i + 1);
            strings[1] = courseId;
            strings[2] = course.getName();
            String[] classesNameArray = sortCourseVo.getClassName().split(" ");
            List<Classes> classesList = classesService.getByName(Arrays.asList(classesNameArray));
            int allStudentNum = 0;
            strings[3] = "";
            StrBuilder classInfo = StrBuilder.create();
            for (Classes classes : classesList) {
                allStudentNum += classes.getStudentNum();
                classInfo.append(classes.getClassname()).append("(").append(classes.getStudentNum()).append(")").append(",");
            }
//            去除最后一个逗号
            strings[3] = classInfo.subString(0, classInfo.length() - 1);
            strings[4] = String.valueOf(allStudentNum);
            strings[5] = StrBuilder.create().append(course.getWeekNum()).append("/").append(course.getTimeWeek()).toStringAndReset();
            strings[6] = String.valueOf(course.getTimeAll());
            strings[7] = String.valueOf(course.getCredit());
            strings[8] = CourseTypeEnum.getContent(course.getType()).getContent();
            strings[9] = course.getNature() == 1 ? "选修" : "必修";
            strings[10] = course.getExaminationWay();
            strings[11] = sortCourseVo.getTeacherName();
            strings[12] = String.valueOf(course.getTimeTheory());
            strings[15] = String.valueOf(course.getTimeAll() - course.getTimeTheory());
            data[i] = strings;
        }
        return data;
    }


    private CellStyle getBaseCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("SimSun");
        font.setFontHeightInPoints((short) 9);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);//自动换行
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        cellStyle.setFont(font);
        return cellStyle;
    }

}
