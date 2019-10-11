package xyz.kingsword.course.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ReUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.VO.SortCourseVo;
import xyz.kingsword.course.dao.ClassesMapper;
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
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.service.SortCourseService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    private ClassesMapper classesMapper;


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
        List<SortCourseVo> sortCourseVoList = sortcourseMapper.getCourseHistory(courseId, nowSemesterId);
        setClassName(sortCourseVoList);
        return sortCourseVoList;
    }

    @Override
//    需要优化
    public List<SortCourseVo> getTeacherHistory(String teacherId) {
        String nowSemesterId = TimeUtil.getFutureSemester().get(0).getId();
        List<SortCourseVo> sortCourseVoList = sortcourseMapper.getTeacherHistory(teacherId, nowSemesterId);
        setClassName(sortCourseVoList);
        return sortCourseVoList;
    }

    @Override
    public PageInfo<SortCourseVo> search(SearchParam param) {
        PageInfo<SortCourseVo> pageInfo = PageHelper.startPage(param.getPageNum(), param.getPageSize()).doSelectPageInfo(() -> sortcourseMapper.search(param));
        List<SortCourseVo> sortCourseVoList = pageInfo.getList();
        setClassName(sortCourseVoList);
        return pageInfo;
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
        JSONArray classIdList = new JSONArray(sortCourseList.size());
        int studentNum = 0;
        for (SortCourse sortCourse : sortCourseList) {
            mergedIdList.add(sortCourse.getId());
            classIdList.addAll(JSON.parseArray(sortCourse.getClassId()));
            studentNum += sortCourse.getStudentNum();
        }
//将课程id去重
        List<Object> collect = classIdList.stream().distinct().collect(toList());
        mainSortCourse.setClassId(JSON.toJSONString(collect));
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

    private void setClassName(List<SortCourseVo> sortCourseVoList) {
        for (SortCourseVo sortCourseVo : sortCourseVoList) {
            List<Classes> classesList = classesMapper.selectByIdList(JSON.parseArray(sortCourseVo.getClassId()).toJavaList(Integer.class));
            String className = classesList.parallelStream().map(Classes::getClassname).collect(Collectors.joining(" "));
            sortCourseVo.setClassName(className);
            sortCourseVo.setSemesterName(TimeUtil.getSemesterName(sortCourseVo.getSemesterId()));
        }
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
            List<Classes> classesList = classesMapper.findByName(Arrays.asList(classNameArray));
            Validator.validateTrue(classesList.size() == classNameArray.length, "第" + (row.getRowNum() + 1) + "行班级名称有误");
            final List<Integer> classIdList = classesList.stream().map(Classes::getId).collect(toList());
            sortCourse.setClassId(JSON.toJSONString(classIdList));
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
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        setExcelHeader(workbook, semesterId);
        CellStyle cellStyle = getBaseCellStyle(workbook);
        String[][] data = renderExportData(semesterId);
        int startRow = 6;
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + startRow);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data[i][j]);
            }
        }
        return workbook;
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
            List<Integer> idList = JSON.parseArray(sortCourseVo.getClassId()).toJavaList(Integer.class);
            List<Classes> classesList = classesMapper.selectByIdList(idList);
            int allStudentNum = 0;
            strings[3] = "";
            StrBuilder classInfo = StrBuilder.create();
            for (Classes classes : classesList) {
                allStudentNum += classes.getStudentNum();
                classInfo.append(classes.getClassname()).append("(").append(classes.getStudentNum()).append(")").append(",");
            }
//            去除最后一个逗号
            strings[3] = classInfo.subString(0, classInfo.length() - 2);
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

    /**
     * 设置表格头信息
     *
     * @param semesterId 学期id
     */
    private void setExcelHeader(Workbook workbook, String semesterId) {
//        创建合并单元格
        Sheet sheet = workbook.getSheetAt(0);
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 20));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 20));
        for (int i = 0; i < 12; i++) {
            sheet.addMergedRegion(new CellRangeAddress(3, 5, i, i));
        }
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 12, 17));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 12, 14));
        sheet.addMergedRegion(new CellRangeAddress(4, 4, 15, 17));
        sheet.addMergedRegion(new CellRangeAddress(3, 5, 18, 18));
        sheet.addMergedRegion(new CellRangeAddress(3, 5, 19, 19));

//        变量按顺序编号
//        第一行
        Font font1 = workbook.createFont();
        font1.setFontHeightInPoints((short) 12);
        font1.setFontName("SimSun");
        font1.setBold(true);
        CellStyle cellStyle1 = getBaseCellStyle(workbook);
        cellStyle1.setFont(font1);

        Font font2 = workbook.createFont();
        font2.setFontHeightInPoints((short) 12);
        font2.setFontName("SimSun");
        CellStyle cellStyle2 = getBaseCellStyle(workbook);
        cellStyle2.setFont(font2);


        Cell cell = sheet.createRow(0).createCell(0);
        cell.setCellStyle(cellStyle1);
        cell.setCellValue("任务通知书");
//        第二行
        cell = sheet.createRow(2).createCell(0);
        cell.setCellStyle(cellStyle2);
        cell.setCellValue(TimeUtil.getSemesterName(semesterId));

        Row row3 = sheet.createRow(3);
        for (int i = 0; i < 12; i++) {
            row3.createCell(i).setCellStyle(getBaseCellStyle(workbook));
        }

        row3.getCell(0).setCellValue("序号");
        row3.getCell(1).setCellValue("课程号");
        row3.getCell(2).setCellValue("课程名");
        row3.getCell(3).setCellValue("合班人数");
        row3.getCell(4).setCellValue("人数");
        row3.getCell(5).setCellValue("周学时/周数");
        row3.getCell(6).setCellValue("总学时");
        row3.getCell(7).setCellValue("学分");
        row3.getCell(8).setCellValue("课程类别");
        row3.getCell(9).setCellValue("课程性质");
        row3.getCell(10).setCellValue("考核方式");
        row3.getCell(11).setCellValue("上课教师");

        row3.createCell(12).setCellStyle(getBaseCellStyle(workbook));
        row3.getCell(12).setCellValue("学时数和教学任务分配");

        Row row4 = sheet.createRow(4);
        row4.createCell(12).setCellStyle(getBaseCellStyle(workbook));
        row4.getCell(12).setCellValue("讲课");

        row4.createCell(15).setCellStyle(getBaseCellStyle(workbook));
        row4.getCell(15).setCellValue("实验、实习、课程设计");

        Row row5 = sheet.createRow(5);
        row5.createCell(12).setCellStyle(getBaseCellStyle(workbook));
        row5.getCell(12).setCellValue("时数");

        row5.createCell(13).setCellStyle(getBaseCellStyle(workbook));
        row5.getCell(13).setCellValue("教师签名");

        row5.createCell(14).setCellStyle(getBaseCellStyle(workbook));
        row5.getCell(14).setCellValue("职称");

        row5.createCell(15).setCellStyle(getBaseCellStyle(workbook));
        row5.getCell(15).setCellValue("时数");

        row5.createCell(16).setCellStyle(getBaseCellStyle(workbook));
        row5.getCell(16).setCellValue("教师签名");

        row5.createCell(17).setCellStyle(getBaseCellStyle(workbook));
        row5.getCell(17).setCellValue("职称");

        row3.createCell(18).setCellStyle(getBaseCellStyle(workbook));
        row3.getCell(18).setCellValue("合班意见");

        row3.createCell(19).setCellStyle(getBaseCellStyle(workbook));
        row3.getCell(19).setCellValue("教室类型");
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
