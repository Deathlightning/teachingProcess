package xyz.kingsword.course.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelReader;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.pojo.ExecutePlan;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.ExcelService;
import xyz.kingsword.course.service.ExecuteService;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service("ExecuteServiceImpl")
public class ExecuteServiceImpl implements ExcelService<ExecutePlan>, ExecuteService {
    /**
     * 执行计划和培养方案进行匹配，不符合的把课程名字存error List
     *
     * @param executePlanList     执行计划
     * @param trainingProgramList 培养方案
     */
    public List<String> verify(List<ExecutePlan> executePlanList, List<TrainingProgram> trainingProgramList) {
        Map<String, List<TrainingProgram>> trainingProgramMap = trainingProgramList.stream().collect(Collectors.groupingBy(TrainingProgram::getCourseId));
        List<String> errorList = new ArrayList<>(executePlanList.size());
        for (ExecutePlan executePlan : executePlanList) {
            TrainingProgram trainingProgram = trainingProgramMap.get(executePlan.getCourseId()).get(0);
            boolean flag1 = ObjectUtil.equal(trainingProgram.getCourseName(), executePlan.getCourseName());
            boolean flag2 = ObjectUtil.equal(trainingProgram.getGrade(), executePlan.getGrade());
            boolean flag3 = ObjectUtil.equal(trainingProgram.getCredit(), executePlan.getCredit());
            boolean flag4 = ObjectUtil.equal(trainingProgram.getTimeAll(), executePlan.getTimeAll());
            boolean flag5 = ObjectUtil.equal(trainingProgram.getTimeTheory(), executePlan.getTimeTheory());
            boolean flag6 = ObjectUtil.equal(trainingProgram.getTimeLab(), executePlan.getTimeLab());
            boolean flag7 = ObjectUtil.equal(trainingProgram.getExaminationWay(), executePlan.getExaminationWay());
            if (flag1 & flag2 & flag3 & flag4 & flag5 & flag6 & flag7) {
                errorList.add(executePlan.getCourseId() + " " + executePlan.getCourseName());
            }
        }
        return errorList;

    }

    @Override
    public List<ExecutePlan> excelImport(InputStream inputStream) {
        ExcelReader excelReader = new ExcelReader(inputStream, 0, true);
        int rowCount = excelReader.getRowCount();
        List<ExecutePlan> executePlanList = new ArrayList<>(rowCount);
        for (int i = 3; i < rowCount - 1; i++) {
            ExecutePlan executePlan = renderExecutePlan(excelReader.readRow(i));
            executePlanList.add(executePlan);
        }
        return executePlanList;
    }

    private ExecutePlan renderExecutePlan(List<Object> data) {
        int startIndex = 0;
        ExecutePlan executePlan = new ExecutePlan();
        executePlan.setCourseId(Convert.toStr(data.get(startIndex + 3)));
        executePlan.setCourseName(Convert.toStr(data.get(startIndex + 4)));
        executePlan.setCredit(Convert.toFloat(data.get(startIndex + 5)));
        executePlan.setTimeAll(Convert.toFloat(data.get(startIndex + 6)));
        executePlan.setTimeTheory(Convert.toFloat(data.get(startIndex + 7)));
        executePlan.setTimeLab(Convert.toFloat(data.get(startIndex + 8)));
        executePlan.setTimeComputer(Convert.toFloat(data.get(startIndex + 9)));
        executePlan.setTimeOther(Convert.toFloat(data.get(startIndex + 10)));
        executePlan.setStartSemester(Convert.toInt(data.get(startIndex + 11)));
        executePlan.setExaminationWay(Convert.toStr(data.get(startIndex + 12)));
        return executePlan;
    }
}
