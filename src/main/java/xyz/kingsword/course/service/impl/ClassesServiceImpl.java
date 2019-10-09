package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.dao.ClassesMapper;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.pojo.Classes;
import xyz.kingsword.course.service.ClassesService;

import java.util.Arrays;
import java.util.Collections;

@Service
public class ClassesServiceImpl implements ClassesService {

    @Autowired
    private ClassesMapper ClassesMapper;

    @Override
    public int addClasses(Classes Classes) {
        return ClassesMapper.insert(Classes);
    }

    @Override
    public int deleteById(Integer id) {
        return ClassesMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int updateById(Classes Classes) {
        return ClassesMapper.updateByPrimaryKey(Classes);
    }

    @Override
    public PageInfo<Classes> getAllClasses(Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> ClassesMapper.selectAll());
    }

    @Override
    public PageInfo<Classes> findByName(String name, Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> ClassesMapper.findByName(Collections.singletonList(name)));
    }
}
