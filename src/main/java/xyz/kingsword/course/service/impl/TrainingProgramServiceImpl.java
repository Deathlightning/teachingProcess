package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.TrainingProgramMapper;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.service.TrainingProgramService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Service
public class TrainingProgramServiceImpl implements TrainingProgramService {
    @Resource
    private TrainingProgramMapper trainingProgramMapper;
    @Resource(name = "TrainingProgramImport")
    private ExcelService<TrainingProgram> excelService;

    @Override
    @Transactional
    public void insert(TrainingProgram record) {
        trainingProgramMapper.insert(record);
    }

    @Override
    @Transactional
    public void insert(List<TrainingProgram> record) {
        trainingProgramMapper.insertList(record);
    }

    @Override
    public void update(TrainingProgram record) {
        trainingProgramMapper.updateByPrimaryKeySelective(record);
    }


    @Override
    public PageInfo<TrainingProgram> select(Integer grade, String courseName, Integer pageNum, Integer pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(() -> trainingProgramMapper.select(grade, courseName));
    }

    @Override
    public TrainingProgram selectById(int id) {
        return trainingProgramMapper.selectByPrimaryKey(id);
    }

    @Override
    public void importExcel(InputStream inputStream) {
        List<TrainingProgram> trainingProgramList = excelService.excelImport(inputStream);
        trainingProgramMapper.insertList(trainingProgramList);
    }

    @Override
    public List<Integer> getGrades() {
        return trainingProgramMapper.getGrades();
    }

    @Override
    public List<TrainingProgram> getByCourseId(List<String> courseIdList) {
        return trainingProgramMapper.getByCourseId(courseIdList);
    }

}
