package xyz.kingsword.course.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.kingsword.course.VO.VerifyField;
import xyz.kingsword.course.VO.VerifyResult;
import xyz.kingsword.course.enmu.ErrorEnum;
import xyz.kingsword.course.exception.BaseException;
import xyz.kingsword.course.exception.DataException;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.pojo.TrainingProgram;
import xyz.kingsword.course.service.ExecutionVerify;
import xyz.kingsword.course.util.ConditionUtil;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExecutionVerifyImpl implements ExecutionVerify {
    /**
     * 验证培养方案与执行计划的不同
     *
     * @param executionPlanList   executionPlanList
     * @param trainingProgramList trainingProgramList
     */
    @Override
    public VerifyResult verify(List<ExecutionPlan> executionPlanList, List<TrainingProgram> trainingProgramList) {
        ConditionUtil.validateTrue(!trainingProgramList.isEmpty()).orElseThrow(() -> new DataException(ErrorEnum.TRAINING_PROGRAM_ERROR));
        ConditionUtil.validateTrue(!executionPlanList.isEmpty()).orElseThrow(() -> new DataException(ErrorEnum.EXECUTION_PLAN_ERROR));
//        预处理
        preprocess(trainingProgramList, executionPlanList);
//        按课程号排序
        trainingProgramList = trainingProgramList.parallelStream().sorted(Comparator.comparing(TrainingProgram::getCourseId)).collect(Collectors.toList());
        executionPlanList = executionPlanList.parallelStream().sorted(Comparator.comparing(ExecutionPlan::getCourseId)).collect(Collectors.toList());

        ConditionUtil.validateTrue(trainingProgramList.size() == executionPlanList.size()).orElseThrow(() -> new BaseException(ErrorEnum.ERROR));
        int len = executionPlanList.size();
        List<Map<String, List<VerifyField>>> differenceList = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            Map<String, List<VerifyField>> map = verifySingleCourse(executionPlanList.get(i), trainingProgramList.get(i));
            differenceList.add(map);
        }
        return VerifyResult.builder().executionResult(differenceList).trainingProgramResult(differenceList).build();
    }

    /**
     * 预处理，解决执行计划和培养方案课程数量，种类不同的问题
     * 处理后的两个list课程数量及种类相同，并按课程号从小到大排序
     *
     * @param trainingProgramList 培养方案list
     * @param executionPlanList   执行计划list
     */
    private void preprocess(List<TrainingProgram> trainingProgramList, List<ExecutionPlan> executionPlanList) {
//        将课程号和课程id转换为set，便于集合做差
        Set<String> trainingProgramSet = trainingProgramList.parallelStream().map(TrainingProgram::getCourseId).collect(Collectors.toSet());
        Set<String> executionPlanSet = executionPlanList.parallelStream().map(ExecutionPlan::getCourseId).collect(Collectors.toSet());
//        trainingProgramSet剩余的是培养方案里面有，但执行计划里面没有的课程id
        trainingProgramSet.removeAll(executionPlanSet);
//        executionPlanSet剩余的是执行计划里面有，但培养方案里面没有的课程id
        executionPlanSet.removeAll(trainingProgramSet);
        executionPlanSet.forEach(v -> trainingProgramList.add(TrainingProgram.builder().courseId(v).build()));
        trainingProgramSet.forEach(v -> executionPlanList.add(ExecutionPlan.builder().courseId(v).build()));
    }

    /**
     * 这样命名可以简化编码，不要滥用
     *
     * @param e 执行计划
     * @param t 培养方案
     * @return 同课程各属性验证情况
     */
    private Map<String, List<VerifyField>> verifySingleCourse(ExecutionPlan e, TrainingProgram t) {
        log.debug("执行计划,{}", e);
        log.debug("培养方案,{}", t);
//        保证两者同专业，同年级，同课程，执行学期相同
//        boolean sameSpeciality = t.getSpecialityId() == e.getSpecialityId();
//        boolean sameGrade = t.getGrade() == e.getGrade();
//        boolean sameSemester = StrUtil.equals(t.getSemesterId(), e.getSemesterId());
        boolean sameCourse = StrUtil.equals(t.getCourseId(), e.getCourseId());
        ConditionUtil.validateTrue(sameCourse).orElseThrow(() -> new DataException(ErrorEnum.VERIFY_ERROR));

        List<VerifyField> verifyFieldList = new ArrayList<>(12);
        verifyFieldList.add(new VerifyField("courseName", Objects.equals(t.getCourseName(), e.getCourseName())));
        verifyFieldList.add(new VerifyField("credit", Objects.equals(t.getCredit(), e.getCredit())));
        verifyFieldList.add(new VerifyField("examinationWay", Objects.equals(t.getExaminationWay(), e.getExaminationWay())));
        verifyFieldList.add(new VerifyField("timeAll", Objects.equals(t.getTimeAll(), e.getTimeAll())));
        verifyFieldList.add(new VerifyField("timeTheory", Objects.equals(t.getTimeTheory(), e.getTimeTheory())));
        verifyFieldList.add(new VerifyField("timeLab", Objects.equals(t.getTimeLab(), e.getTimeLab())));
        verifyFieldList.add(new VerifyField("timeComputer", Objects.equals(t.getTimeComputer(), e.getTimeComputer())));
        verifyFieldList.add(new VerifyField("timeOther", Objects.equals(t.getTimeOther(), e.getTimeOther())));
        verifyFieldList.add(new VerifyField("startSemester", Objects.equals(t.getStartSemester(), e.getStartSemester())));

        return MapUtil.builder(t.getCourseId(), verifyFieldList).build();
    }
}
