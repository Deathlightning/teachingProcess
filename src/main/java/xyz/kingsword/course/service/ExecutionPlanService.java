package xyz.kingsword.course.service;

import com.github.pagehelper.PageInfo;
import xyz.kingsword.course.pojo.ExecutionPlan;

import java.util.List;

public interface ExecutionPlanService {
    void insert(List<ExecutionPlan> executionPlanList);

    void update(ExecutionPlan executionPlan);

    PageInfo<ExecutionPlan> getExceptionExecutionPlan();
}
