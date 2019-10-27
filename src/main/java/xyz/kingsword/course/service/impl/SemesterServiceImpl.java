package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.SemesterMapper;
import xyz.kingsword.course.pojo.Semester;
import xyz.kingsword.course.service.SemesterService;
import xyz.kingsword.course.util.TimeUtil;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterMapper semesterMapper;

//    @Bean
//    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
//        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//        converter.setObjectMapper(mapper);
//        return converter;
//    }//添加转换器@Override


    @Override
    @Transactional
    public void addSemester(Semester semester) {
        semester.setName(TimeUtil.getSemesterName(semester.getId()));
        semesterMapper.insert(semester);
    }


    @Override
    public int updateById(Semester semester) {
        return semesterMapper.updateById(semester);
    }

    @Override
    public PageInfo<Semester> getAllSemester(Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> semesterMapper.selectAll());
    }

    @Override
    public PageInfo<Semester> getFutureSemester(Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> semesterMapper.getFutureSemester());
    }

    @Override
    public PageInfo<Semester> findByName(String name, Integer pageNumber, Integer pageSize) {
        return PageHelper.startPage(pageNumber, pageSize).doSelectPageInfo(() -> semesterMapper.findByName(name));
    }
}
