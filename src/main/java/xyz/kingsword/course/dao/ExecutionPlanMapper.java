package xyz.kingsword.course.dao;

import xyz.kingsword.course.pojo.ExecutionPlan;

import java.util.List;

public interface ExecutionPlanMapper {
    int insert(List<ExecutionPlan> executionPlanList);

    int insertSelective(ExecutionPlan record);

    ExecutionPlan selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ExecutionPlan record);

    int updateByPrimaryKey(ExecutionPlan record);

    List<ExecutionPlan> getExceptionExecutionPlan();
}