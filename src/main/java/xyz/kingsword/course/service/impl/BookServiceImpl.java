package xyz.kingsword.course.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.BookMapper;
import xyz.kingsword.course.dao.CourseMapper;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Book;
import xyz.kingsword.course.pojo.BookOrder;
import xyz.kingsword.course.pojo.Course;
import xyz.kingsword.course.pojo.DO.BookExportViewDo;
import xyz.kingsword.course.pojo.DO.BookOrderDo;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.TimeUtil;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private CourseMapper courseMapper;
    /**
     * 在校的班级数量
     */
    private final int CLASS_START_INDEX = 15;


    /**
     * 表示订书的班级有哪些
     * 每次导出书籍订购信息需要更新该值
     * 仅使用set方法进行更新
     */
    private String[] classArray;

    private void setClassArray(String[] classArray) {
        this.classArray = Optional.ofNullable(classArray).orElse(new String[0]);
    }

    /**
     * 更新为老师留几本书
     *
     * @param num 新的数量
     * @param id  教材id
     */
    @Override
    public void updateForTeacher(int num, int id) {
        bookMapper.updateForTeacher(num, id);
    }

    @Override
    public List<Book> getTextBook(String courseId) {
        Course course = courseMapper.findCourseById(courseId).get();
        String bookListJson = course.getTextBook();
        if (bookListJson.length() > 2)
            return bookMapper.selectBookList(JSON.parseArray(bookListJson, Integer.class));
        return new ArrayList<>();
    }

    @Override
    public List<Book> getReferenceBook(String courseId) {
        Course course = courseMapper.findCourseById(courseId).get();
        String bookListJson = course.getReferenceBook();
        if (bookListJson.length() > 2)
            return bookMapper.selectBookList(JSON.parseArray(bookListJson, Integer.class));
        return new ArrayList<>();
    }

    @Override
    public List<Book> getByIdList(List<Integer> idList) {
        return idList == null ? new ArrayList<>(0) : bookMapper.selectBookList(idList);
    }

    @Override
    public List<Book> getBookOrder(String studentId, String semesterId) {
        return bookMapper.getBookOrder(studentId, semesterId);
    }

    @Override
    public void purchase(List<BookOrder> bookOrderList) {
        int flag = bookMapper.purchase(bookOrderList);
        log.debug("插入，{}", flag);
    }


    @Override
    public void update(Book book) {
        bookMapper.update(book);
    }

    @Override
    @Transactional
    public void insert(Book book, String courseId) {
        int flag = bookMapper.insert(book);
        ConditionUtil.validateTrue(flag == 1).orElseThrow(() -> new DataException(ErrorEnum.ERROR));
        courseMapper.addCourseBook(book.getId(), courseId);
    }

    @Override
    public void delete(int id) {
        int flag = bookMapper.delete(id);
        log.debug("delete ,{}", flag);
    }

    @Override
    public Book getBook(int id) {
        return bookMapper.selectBookByPrimaryKey(id);
    }

    /**
     * 构建workBook进行excel导出
     */
    @Override
    public Workbook exportBookSubscription(String semesterId) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("templates/orderDetail.xlsx");
        Workbook workbook;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Sheet sheet = workbook.getSheetAt(0);
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

        sheet.getRow(0).getCell(0).setCellValue(TimeUtil.getSemesterName(semesterId) + "教材订购详情");
        //        设置班级
        System.out.println(Arrays.toString(classArray));
        Row rowHead = sheet.getRow(1);
        for (int i = 0; i < classArray.length; i++) {
            Cell cell = rowHead.createCell(CLASS_START_INDEX + i);
            cell.setCellStyle(getBaseCellStyle(workbook));
            cell.setCellValue(classArray[i]);
        }
        return workbook;
    }


    /**
     * 构建导出excel所需数据
     *
     * @return String[][]
     */
    private String[][] renderData(String semesterId) {
        List<BookOrderDo> bookOrderDoList = bookMapper.getClassBookOrder(semesterId);
        String[] classArray = bookOrderDoList.parallelStream()
                .map(BookOrderDo::getClassName)
                .distinct()
                .sorted((a, b) -> StrUtil.compare(a, b, true))
                .toArray(String[]::new);

        setClassArray(classArray);

        Map<String, Integer> classNameToCellIndex = new HashMap<>(this.classArray.length);
        List<String> classList = Arrays.asList(classArray);
        for (int i = 0; i < classList.size(); i++) {
            classNameToCellIndex.put(classList.get(i), i + CLASS_START_INDEX);
        }
        List<Integer> allBookIdList = bookOrderDoList.parallelStream().map(BookOrderDo::getBookId).collect(Collectors.toList());
//        主键为书籍id，便于搜索
        Map<Integer, Book> bookMap = bookMapper.selectBookList(allBookIdList)
                .parallelStream().collect(Collectors.toMap(Book::getId, v -> v));
        List<BookExportViewDo> bookExportViewDoList = bookMapper.getCourseInfo(semesterId);

        Map<String, List<BookExportViewDo>> courseMap = bookExportViewDoList.parallelStream()
                .collect(Collectors.groupingBy(BookExportViewDo::getCourseId));

        int length = CLASS_START_INDEX + this.classArray.length;
        String[][] data = new String[courseMap.size()][];
        for (int i = 0; i < courseMap.size(); i++) {
            BookExportViewDo bookExportViewDo = bookExportViewDoList.get(i);
            String courseId = bookExportViewDo.getCourseId();
            String[] strings = new String[length];
            Arrays.fill(strings, "");
            strings[0] = courseId;
            strings[1] = bookExportViewDo.getCourseName();
            strings[2] = bookExportViewDo.getNature();
            strings[3] = bookExportViewDo.getClassName().replace(" ", "\n");
            List<Integer> bookIdList = bookExportViewDo.getTextBookList();
            if (bookIdList.size() > 0) {
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
//筛选出与当前课本有关的订单，再对班级数量分组求和
                    Map<String, Long> map = bookOrderDoList.parallelStream().filter(v -> v.getBookId() == bookId).collect(Collectors.groupingBy(BookOrderDo::getClassName, Collectors.counting()));
                    map.forEach((a, b) -> strings[classNameToCellIndex.get(a)] = StrBuilder.create(strings[classNameToCellIndex.get(a)]).append(b).append("\n").toStringAndReset());
                    data[i] = strings;
                }
                strings[4] = isbn.toStringAndReset();
                strings[5] = name.toStringAndReset();
                strings[6] = price.toStringAndReset();
                strings[7] = author.toStringAndReset();
                strings[8] = publish.toStringAndReset();
                strings[9] = pubDate.toStringAndReset();
                strings[10] = edition.toStringAndReset();
                strings[11] = award.toStringAndReset();
                strings[12] = courseMap.get(bookExportViewDo.getCourseId())
                        .parallelStream().map(BookExportViewDo::getTeacherName)
                        .distinct().collect(Collectors.joining("\n"));
                strings[13] = forTeacher.toStringAndReset();
            }
            strings[14] = bookIdList.size() > 0 ? "是" : "否";
            data[i] = strings;
        }
        return data;
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
}
