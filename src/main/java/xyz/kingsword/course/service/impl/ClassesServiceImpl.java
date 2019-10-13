package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.ClassesMapper;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.service.ClassesService;
import xyz.kingsword.course.util.ConditionUtil;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ClassesMapper classesMapper;

    private List<Classes> classesList;

    @PostConstruct
    public void init() {
        classesList = classesMapper.selectAll();
        ConditionUtil.validateTrue(!classesList.isEmpty()).orElseThrow(DataException::new);
    }

    @Override
    @Transactional
    public void insert(List<Classes> classesList) {
        int flag = classesMapper.insertList(classesList);
        this.classesList.addAll(classesList);
        log.debug("新增班级,{}", flag);
    }

    @Override
    public void updateById(Classes Classes) {
        int flag = classesMapper.updateByPrimaryKey(Classes);
        log.debug("更新班级,{}", flag);
    }

    @Override
    public PageInfo<Classes> getAllClasses(Integer pageNumber, Integer pageSize) {
        PageInfo<Classes> pageInfo = PageInfo.of(classesList);
        pageInfo.setPageNum(pageNumber);
        pageInfo.setPageSize(pageSize);
        return pageInfo;
    }

    @Override
    public List<Classes> getByName(List<String> nameList) {
        return classesList.parallelStream().filter(v -> nameList.contains(v.getClassname())).collect(Collectors.toList());
    }

    @Override
    public List<Classes> getSchoolClass() {
        final int year = LocalDate.now().getYear();
        return classesList.parallelStream().filter(v -> year - v.getAdmissionTime() < 4).collect(Collectors.toList());
    }
}
