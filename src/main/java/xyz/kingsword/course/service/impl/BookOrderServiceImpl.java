package xyz.kingsword.course.service.impl;


import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.BookOrderVo;
import xyz.kingsword.course.VO.StudentVo;
import xyz.kingsword.course.dao.*;
import xyz.kingsword.course.enmu.CourseNature;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.CourseGroup;
import xyz.kingsword.course.pojo.User;
import xyz.kingsword.course.pojo.param.CourseGroupSelectParam;
import xyz.kingsword.course.pojo.param.StudentSelectParam;
import xyz.kingsword.course.service.BookOrderService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.SpringContextUtil;
import xyz.kingsword.course.util.TimeUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookOrderServiceImpl implements BookOrderService {
    @Resource
    private BookOrderMapper bookOrderMapper;
    @Resource
    private CourseGroupMapper courseGroupMapper;
    @Resource
    private BookMapper bookMapper;
    @Resource
    private ConfigMapper configMapper;

    /**
     * @param bookOrderList
     * @return
     */
    @Override
    public List<Integer> insert(List<BookOrder> bookOrderList) {
//        ConditionUtil.validateTrue(purchaseStatusCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
        bookOrderMapper.insert(bookOrderList);
        List<Integer> orderIdList = new ArrayList<>(bookOrderList.size());
        List<Integer> bookIdList = new ArrayList<>(bookOrderList.size());
        for (BookOrder bookOrder : bookOrderList) {
            orderIdList.add(bookOrder.getId());
            bookIdList.add(bookOrder.getBookId());
        }
//如果是老师订书，就给forTeacher自增
        Optional.ofNullable(UserUtil.getTeacher())
                .ifPresent(v -> bookMapper.teacherPurchase(bookIdList.parallelStream().distinct().collect(Collectors.toList())));
        return orderIdList;
    }

    /**
     * 取消订购
     *
     * @param orderId 订购记录id
     */
    @Override
    public void cancelPurchase(int orderId) {
//        ConditionUtil.validateTrue(!purchaseStatusCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
        int num = bookOrderMapper.delete(orderId);
        log.debug("取消订购,{}", num);
        Optional.ofNullable(UserUtil.getTeacher()).ifPresent(v -> bookMapper.cancelTeacherPurchase(orderId));
    }

    @Override
    public List<BookOrderVo> select(String studentId, String semesterId, String className) {
        return bookOrderMapper.select(studentId, semesterId, className);
    }

    @Override
    public List<BookOrderVo> selectByTeacher(String teacherId, String semesterId) {
        return bookOrderMapper.selectByTeacher(teacherId, semesterId);
    }

    /**
     * 订书开关验证，只针对学生
     */
    private boolean purchaseStatusCheck() {
        User user = UserUtil.getUser();
        Integer roleId = user.getCurrentRole();
        if (roleId != null && roleId == RoleEnum.STUDENT.getCode()) {
            return configMapper.selectPurchaseStatus();
        }
        return true;
    }

    private Map<String, Integer> classIndex = new HashMap<>();
    private final int CLASS_START_INDEX = 15;

    @Override
    public Workbook exportAllStudentRecord(String semesterId) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/orderDetail.xlsx");
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);
        CellStyle cellStyle = getBaseCellStyle(workbook);
        sheet.getRow(0).getCell(0).setCellValue(TimeUtil.getSemesterName(semesterId) + "教材订购详情");
        List<String> classNameList = bookOrderMapper.purchaseClass(semesterId);
        Row rowHead = sheet.getRow(1);
        for (int i = 0; i < classNameList.size(); i++) {
            int index = CLASS_START_INDEX + i;
            Cell cell = rowHead.createCell(index);
            cell.setCellStyle(getBaseCellStyle(workbook));
            cell.setCellValue(classNameList.get(i));
            classIndex.put(classNameList.get(i), index);
        }

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

    /**
     * 构建导出excel所需数据
     *
     * @return String[][]
     */
    private String[][] renderData(String semesterId) {
        List<CourseGroup> courseGroupList = courseGroupMapper.select(CourseGroupSelectParam.builder().semesterId(semesterId).build());
        List<BookOrderVo> bookOrderVoList = bookOrderMapper.select(null, semesterId, null);
        Map<Integer, Map<String, Long>> bookIdToClass = bookOrderVoList
                .parallelStream()
                .filter(v -> v.getClassName() != null)
                .collect(Collectors.groupingBy(BookOrderVo::getBookId, Collectors.groupingBy(BookOrderVo::getClassName, Collectors.counting())));

        Map<String, List<CourseGroup>> courseMap = courseGroupList.parallelStream().collect(Collectors.groupingBy(CourseGroup::getCouId));
        List<Integer> idList = courseGroupList.parallelStream().flatMap(v -> v.getTextBook().stream()).collect(Collectors.toList());
        ConditionUtil.notEmpty(idList).orElseThrow(() -> new BaseException("没有教材数据"));
//        主键为书籍id，便于搜索
        Map<Integer, Book> bookMap = bookMapper.selectBookList(idList)
                .parallelStream().collect(Collectors.toMap(Book::getId, v -> v));

        int length = CLASS_START_INDEX + this.classIndex.size();
        String[][] data = new String[courseMap.size()][];
        int i = 0;
        for (String courseId : courseMap.keySet()) {
            List<CourseGroup> courseGroupListItem = courseMap.get(courseId);
            String[] strings = new String[length];
//            数据初始化为空字符串，避免导出null
            Arrays.fill(strings, "");
            StrBuilder classStrBuilder = StrBuilder.create();
            StrBuilder teacherStrBuilder = StrBuilder.create();
            for (CourseGroup courseGroup : courseGroupListItem) {
                String className = courseGroup.getClassName().replace(" ", "\n");
                classStrBuilder.append(className).append("\n");
                teacherStrBuilder.append(courseGroup.getTeacherName()).append("\n");
            }
            CourseGroup courseGroup = courseGroupListItem.get(0);
            strings[0] = courseId;
            strings[1] = courseGroup.getCourseName();
            strings[2] = CourseNature.getContent(courseGroup.getCourseNature()).getContent();
            strings[3] = classStrBuilder.toStringAndReset();
            List<Integer> bookIdList = courseGroupListItem.get(0).getTextBook();
            if (!bookIdList.isEmpty()) {
                StrBuilder isbn = new StrBuilder();
                StrBuilder name = new StrBuilder();
                StrBuilder price = new StrBuilder();
                StrBuilder author = new StrBuilder();
                StrBuilder publish = new StrBuilder();
                StrBuilder pubDate = new StrBuilder();
                StrBuilder edition = new StrBuilder();
                StrBuilder award = new StrBuilder();
                StrBuilder forTeacher = new StrBuilder();
                for (int bookId : bookIdList) {
                    Book book = bookMap.get(bookId);
                    isbn.append(book.getIsbn()).append("\n");
                    name.append(book.getName()).append("\n");
                    price.append(book.getPrice()).append("\n");
                    author.append(book.getAuthor()).append("\n");
                    publish.append(book.getPublish()).append("\n");
                    pubDate.append(book.getPubDate()).append("\n");
                    edition.append(book.getEdition()).append("\n");
                    award.append(book.getAward()).append("\n");
                    forTeacher.append(book.getForTeacher()).append("\n");
                    if (!classIndex.isEmpty()) {
                        Map<String, Long> classToNum = bookIdToClass.get(bookId);
                        if (classToNum != null && !classToNum.isEmpty()) {
                            classToNum.forEach((className, num) -> strings[classIndex.get(className)] = StrBuilder.create(strings[classIndex.get(className)]).append(num).append("\n").toStringAndReset());
                        }
                    }
                }
                strings[4] = isbn.toStringAndReset();
                strings[5] = name.toStringAndReset();
                strings[6] = price.toStringAndReset();
                strings[7] = author.toStringAndReset();
                strings[8] = publish.toStringAndReset();
                strings[9] = pubDate.toStringAndReset();
                strings[10] = edition.toStringAndReset();
                strings[11] = award.toStringAndReset();
                strings[12] = teacherStrBuilder.toStringAndReset();
                strings[13] = forTeacher.toStringAndReset();
            }
            strings[14] = bookIdList.size() > 0 ? "是" : "否";
            data[i++] = strings;
        }
        return data;
    }


    private CellStyle getBaseCellStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);//自动换行
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        cellStyle.setFont(font);
        return cellStyle;
    }

    @Override
    public Workbook exportSingleRecord(String studentId) {
        return null;
    }

    @Override
    public Workbook exportClassRecord(String className, String semesterId) {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        CellStyle cellStyle = getBaseCellStyle(workbook);
        String[][] data = renderData(className, semesterId);
        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j]);
                cell.setCellStyle(cellStyle);
            }
        }
        return workbook;
    }

    private String[][] renderData(String className, String semesterId) {
        List<BookOrderVo> bookOrderVoList = this.select(null, semesterId, className);
        StudentMapper studentMapper = SpringContextUtil.getBean(StudentMapper.class);
        List<StudentVo> studentList = studentMapper.select(StudentSelectParam.builder().className(className).pageSize(0).build())
                .parallelStream()
                .sorted((a, b) -> StrUtil.compare(a.getId(), b.getId(), false))
                .collect(Collectors.toList());
        List<String> bookNameList = bookOrderVoList.parallelStream().map(BookOrderVo::getName).distinct().collect(Collectors.toList());
        String[][] data = new String[studentList.size() + 1][bookNameList.size() + 1];
//        数据初始化
        for (int i = 0; i < data.length; i++) {
            data[i] = new String[bookNameList.size() + 1];
            Arrays.fill(data[i], "0");
        }
        data[0][0] = "";
//         第一行书名
        Map<String, Integer> bookNameToIndex = new HashMap<>(bookNameList.size());
        for (int i = 0; i < bookNameList.size(); i++) {
            bookNameToIndex.put(bookNameList.get(i), i + 1);
            data[0][i + 1] = bookNameList.get(i);
        }
//      第一列学生名字
        for (int i = 1; i < studentList.size() + 1; i++) {
            data[i][0] = studentList.get(i - 1).getName();
        }

        Map<String, List<BookOrderVo>> studentToOrder = bookOrderVoList.parallelStream().collect(Collectors.groupingBy(BookOrderVo::getUserId));
        for (StudentVo studentVo : studentList) {
            List<BookOrderVo> orderList = studentToOrder.get(studentVo.getId());
            if (orderList != null && !orderList.isEmpty()) {
                for (BookOrderVo bookOrderVo : orderList) {
                    int bookIndex = bookNameToIndex.get(bookOrderVo.getName());
                    int studentIndex = studentList.indexOf(studentVo) + 1;
                    data[studentIndex][bookIndex] = "1";
                }
            }
        }
        return data;
    }
}
