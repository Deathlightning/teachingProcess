package xyz.kingsword.course.service.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.service.ClassesService;
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService, ExcelService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private ClassesService classesService;
    /**
     * 在校的班级数量
     */
    private int classSize = 0;

    @Override
    public Book getBookById(int id) {
        return bookMapper.selectBookByPrimaryKey(id);
    }

    /**
     * 构建workBook进行excel导出
     */
    @Override
    public Workbook exportBookSubscription(String semesterId) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        setExcelHeader(workbook, semesterId);
        CellStyle cellStyle = getBaseCellStyle(workbook);
        String[][] data = renderData(semesterId);
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 2);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(data[i][j]);
            }
        }
        return workbook;
    }

    private void setExcelHeader(Workbook workbook, String semesterId) {
        Sheet sheet = workbook.getSheetAt(0);
        Row row1 = sheet.createRow(0);
        Row row2 = sheet.createRow(1);
        CellStyle cellStyle1 = getBaseCellStyle(workbook);
        CellStyle cellStyle2 = getBaseCellStyle(workbook);
        Font font1 = workbook.createFont();//第一行用的
        Font font2 = workbook.createFont();//第二行用的
        font1.setFontHeightInPoints((short) 26);
        font2.setFontHeightInPoints((short) 10);
        font1.setBold(true);
        font2.setBold(true);
        font1.setFontName("宋体");
        font2.setFontName("宋体");
        cellStyle1.setFont(font1);
        cellStyle2.setFont(font2);

        List<String> classNameList = classesService.getSchoolClass().parallelStream().map(Classes::getClassname).collect(Collectors.toList());
        this.classSize = classNameList.size();
        String[] row2Content = new String[15 + classSize];
        row2Content[0] = "课程号";
        row2Content[1] = "课程名";
        row2Content[2] = "课程课程性质";
        row2Content[3] = "课程使用班级";
        row2Content[4] = "教材书号";
        row2Content[5] = "教材名称";
        row2Content[6] = "单价";
        row2Content[7] = "主编";
        row2Content[8] = "教材出版社";
        row2Content[9] = "出版时间";
        row2Content[10] = "版次";
        row2Content[11] = "获奖情况";
        row2Content[12] = "使用教师";
        row2Content[13] = "为老师预留数量";
        row2Content[14] = "任课教师是否需要订书";
        for (int i = 0; i < classNameList.size(); i++) {
            row2Content[15 + i] = classNameList.get(i);
        }
        for (int i = 0; i < row2Content.length; i++) {
            Cell cell1 = row1.createCell(i);
            cell1.setCellStyle(cellStyle1);

            Cell cell2 = row2.createCell(i);
            cell2.setCellStyle(cellStyle2);
            cell2.setCellValue(row2Content[i]);
        }
        row1.getCell(0).setCellValue(TimeUtil.getSemesterName(semesterId) + "教材征订计划");
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, row2Content.length);// 下标从0开始 起始行号，终止行号， 起始列号，终止列号
        sheet.addMergedRegion(region);
    }

    /**
     * 构建导出excel所需数据
     *
     * @return String[][]
     */
    private String[][] renderData(String semesterId) {
        List<CourseBook> courseBookList = bookMapper.getCourseBook(semesterId);

        String[][] data = new String[courseBookList.size()][];
        int length = courseBookList.size();
        for (int i = 0; i < length; i++) {
            String[] strings = new String[classSize];
            strings[0] = courseBookList.get(i).getCourseId();
            strings[1] = courseBookList.get(i).getCourseName();
            strings[2] = courseBookList.get(i).getNature() == 1 ? "选修课" : "必修课";
            strings[3] = courseBookList.get(i).getClassName().replace(" ", "\n");
            strings[4] = "";
            strings[5] = "";
            strings[6] = "";
            strings[7] = "";
            strings[8] = "";
            strings[9] = "";
            strings[10] = "";
            strings[11] = "";
            List<Integer> bookIdList = JSONArray.parseArray(courseBookList.get(i).getTextBook()).toJavaList(Integer.class);
            strings[14] = bookIdList.size() > 0 ? "是" : "否";
            if (bookIdList.size() > 0) {
                List<Book> bookList = bookMapper.selectBookList(bookIdList);
                for (Book book : bookList) {
                    strings[4] = strings[4] + book.getIsbn() + "\n";
                    strings[5] = strings[5] + book.getName() + "\n";
                    strings[6] = strings[6] + book.getPrice() + "\n";
                    strings[7] = strings[7] + book.getAuthor() + "\n";
                    strings[8] = strings[8] + book.getPublish() + "\n";
                    strings[9] = strings[9] + book.getPubDate() + "\n";
                    strings[10] = strings[10] + book.getEdition() + "\n";
                    strings[11] = strings[11] + book.getAward() + "\n";
                }
//                主动释放查询结果，触发jvm垃圾收集，避免循环造成内存占用过高
                bookList = null;
                bookIdList = null;
            }
            List<TeacherGroup> teacherGroupList = courseMapper.selectTeacherGroup(semesterId, courseBookList.get(i).getCourseId());
            teacherGroupList.forEach(v -> strings[12] = strings[12] + v.getTeacherName() + "\n");
            strings[13] = String.valueOf(teacherGroupList.size());
            data[i] = strings;
        }
        return data;
    }

    @Override
    public void updateForTeacher(int num, int id) {
        bookMapper.updateForTeacher(num, id);
    }

    @Override
    public List<Book> selectBookList(List<Integer> idList) {
        return idList == null || idList.isEmpty() ? new ArrayList<>() : bookMapper.selectBookList(idList);
    }

    @Override
    public void updateBook(Book book) {
        bookMapper.update(book);
    }

    @Override
    public void insert(Book book, String courseId) {
        int flag = bookMapper.insert(book);
        ConditionUtil.validateTrue(flag == 1).orElseThrow(DataException::new);
        courseMapper.addCourseBook(book.getId(), courseId);
    }

    @Override
    public List<TrainingProgram> excelImport(InputStream inputStream) {
        return null;
    }

    /**
     * 需要对教材管理进行权限控制，一个课程组只能一个人报教材，哪个老师先报就进行授权，其他人不能报
     *
     * @param teaId    欲进行操作的教师id
     * @param courseId 通过课程号查教材管理者是谁
     * @return boolean
     */
    private boolean validateAuth(String teaId, String courseId) {
        return false;
    }

    private CellStyle getBaseCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);//自动换行
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        return cellStyle;
    }
}
