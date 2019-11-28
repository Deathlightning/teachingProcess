package xyz.kingsword.course.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.*;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.enmu.RoleEnum;
import xyz.kingsword.course.exception.AuthException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.exception.OperationException;
import xyz.kingsword.course.pojo.*;
import xyz.kingsword.course.service.BookService;
import xyz.kingsword.course.util.ConditionUtil;
import xyz.kingsword.course.util.SpringContextUtil;
import xyz.kingsword.course.util.UserUtil;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    @Resource
    private BookMapper bookMapper;
    @Resource
    private CourseMapper courseMapper;
    @Resource
    private ConfigMapper configMapper;

    @Resource(name = "book")
    private Cache bookCache;

    @Override
    public List<Book> getTextBook(String courseId) {
        Optional<Course> optional = courseMapper.getByPrimaryKey(courseId);
        if (optional.isPresent()) {
            Course course = optional.get();
            String bookListJson = course.getTextBook();
            return getByIdList(bookListJson);
        }
        return new ArrayList<>();
    }

    @Override
    public List<Book> getReferenceBook(String courseId) {
        Optional<Course> optional = courseMapper.getByPrimaryKey(courseId);
        if (optional.isPresent()) {
            Course course = optional.get();
            String bookListJson = course.getReferenceBook();
            return getByIdList(bookListJson);
        }
        return new ArrayList<>();
    }

    /**
     * 先读缓存，遇到没有的再去数据库拿
     *
     * @param idList idList
     * @return List<Book>
     */
    @Override
    public List<Book> getByIdList(Collection<Integer> idList) {
        List<Book> bookList = new ArrayList<>(idList.size());
        Iterator<Integer> iterator = idList.iterator();
        while (iterator.hasNext()) {
            Book book = bookCache.get(iterator.next(), Book.class);
            if (book != null) {
                bookList.add(book);
                iterator.remove();
            }
        }
        if (!idList.isEmpty()) {
            List<Book> bookListDb = bookMapper.selectBookList(idList);
            bookList.addAll(bookListDb);
        }
        return bookList;
    }

    @Override
    public Map<Integer, Book> getMap(Collection<Integer> idList) {
        Map<Integer, Book> map = new HashMap<>(idList.size());
        Iterator<Integer> iterator = idList.iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            Optional.ofNullable(bookCache.get(id, Book.class)).ifPresent(v -> {
                map.put(id, v);
                iterator.remove();
            });
        }
        if (!idList.isEmpty()) {
            List<Book> bookListDb = bookMapper.selectBookList(idList);
            bookListDb.forEach(v -> map.put(v.getId(), v));
        }
        return map;
    }

    @Override
    public List<Book> getByIdList(String json) {
        return json != null && json.length() > 2 ? getByIdList(JSONArray.parseArray(json, Integer.class)) : new ArrayList<>();
    }

    @Override
    @Cacheable(cacheNames = "book", key = "#id")
    public Book getBook(int id) {
        System.out.println(bookCache.get(id, Book.class));
        return bookMapper.selectBookByPrimaryKey(id);
    }


    @Override
    public void setDeclareStatus(boolean flag) {
        ConditionUtil.validateTrue(!flag || !getPurchaseStatus()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_FORBIDDEN));
        configMapper.setDeclareStatus(flag);
    }

    @Override
    public void setPurchaseStatus(boolean flag) {
        ConditionUtil.validateTrue(!flag || !getDeclareStatus()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_FORBIDDEN));
        configMapper.setPurchaseStatus(flag);
    }

    @Override
    public boolean getDeclareStatus() {
        return configMapper.selectDeclareStatus();
//        return Optional.ofNullable(configCache.get("declareStatus", Boolean.class)).orElse(false);
    }


    @Override
    public boolean getPurchaseStatus() {
        return configMapper.selectPurchaseStatus();
//        return Optional.ofNullable(configCache.get("purchaseStatus", Boolean.class)).orElse(false);
    }


    @Override
    @CachePut(cacheNames = "book", key = "#result.id")
    public Book update(Book book) {
//        ConditionUtil.validateTrue(declareCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
        bookMapper.update(book);
        return book;
    }

    /**
     * 新增教材，默认会给每一位老师都订书
     *
     * @param book     book
     * @param courseId courseId
     */
    @Override
    @Transactional
    @CachePut(cacheNames = "book", key = "#result.id")
    public Book insert(Book book, String courseId) {
        validateAuth(courseId);
        CourseGroupMapper courseGroupMapper = SpringContextUtil.getBean(CourseGroupMapper.class);
        BookOrderMapper bookOrderMapper = SpringContextUtil.getBean(BookOrderMapper.class);
        List<CourseGroup> courseGroupList = courseGroupMapper.getNextSemesterCourseGroup(courseId);
        book.setForTeacher(courseGroupList.size());
        bookMapper.insert(book);
        int bookId = book.getId();
        courseMapper.addCourseBook(bookId, courseId);

        List<BookOrder> bookOrderList = new ArrayList<>(courseGroupList.size());
        for (CourseGroup courseGroup : courseGroupList) {
            BookOrder bookOrder = new BookOrder();
            bookOrder.setUserId(courseGroup.getTeaId());
            bookOrder.setBookId(bookId);
            bookOrder.setCourseId(courseId);
            bookOrder.setSemesterId(courseGroup.getSemesterId());
            bookOrderList.add(bookOrder);
        }
        bookOrderMapper.insert(bookOrderList);
        return book;
    }

    @Override
    public void delete(List<Integer> idList, String courseId) {
        if (idList != null && !idList.isEmpty()) {
//            ConditionUtil.validateTrue(declareCheck()).orElseThrow(() -> new OperationException(ErrorEnum.OPERATION_TIME_FORBIDDEN));
            int flag = bookMapper.delete(idList);
            log.debug("删除书籍，{}", flag);
            idList.forEach(v -> bookCache.evict(v));

            Course course = courseMapper.getByPrimaryKey(courseId).orElseThrow(DataException::new);
            String textBookStr = course.getTextBook();
            if (textBookStr != null && !textBookStr.isEmpty()) {
                List<Integer> textBookIdList = JSONArray.parseArray(textBookStr, Integer.class);
                textBookIdList.removeAll(idList);
                courseMapper.setTextBook(JSON.toJSONString(textBookIdList), courseId);
            }
        }
    }


    /**
     * 报教材开关验证
     */
    private boolean declareCheck() {
        User user = UserUtil.getUser();
        Integer roleId = user.getCurrentRole();
        if (roleId != null && roleId == RoleEnum.ACADEMIC_MANAGER.getCode()) {
            return true;
        }
        return getDeclareStatus();

    }

    /**
     * 需要对教材管理进行权限控制，一个课程组只能一个人报教材，哪个老师先报就进行授权，其他人不能报，对教学部不做限制
     *
     * @param courseId 通过课程号查教材管理者是谁
     */
    private void validateAuth(String courseId) {
        User user = UserUtil.getUser();
        Integer roleId = user.getCurrentRole();
        if (roleId != null && roleId == RoleEnum.ACADEMIC_MANAGER.getCode()) {
            return;
        }
        User teacher = UserUtil.getUser();
        String teacherId = courseMapper.getBookManager(courseId);
        if (teacherId == null) {
            courseMapper.setBookManager(courseId, user.getUsername());
            return;
        }
        ConditionUtil.validateTrue(StrUtil.equals(teacherId, teacher.getUsername())).orElseThrow(AuthException::new);
    }
}
