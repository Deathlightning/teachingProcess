package xyz.kingsword.course.service;

import xyz.kingsword.course.pojo.ExecutePlan;
import xyz.kingsword.course.pojo.TrainingProgram;

import java.io.InputStream;
import java.util.List;

public interface ExecuteService {
    List<String> verify(List<ExecutePlan> executePlanList, List<TrainingProgram> trainingProgramList);

    List<ExecutePlan> excelImport(InputStream inputStream);

}
