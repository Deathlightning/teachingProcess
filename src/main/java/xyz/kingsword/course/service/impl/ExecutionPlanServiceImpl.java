package xyz.kingsword.course.service.impl;

import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import xyz.kingsword.course.dao.ExecutionPlanMapper;
import xyz.kingsword.course.pojo.ExecutionPlan;
import xyz.kingsword.course.service.ExecutionPlanService;
import xyz.kingsword.course.util.ConditionUtil;

import javax.annotation.Resource;
import java.util.List;

public class ExecutionPlanServiceImpl implements ExecutionPlanService {
    @Resource
    private ExecutionPlanMapper executionPlanMapper;

    @Override
    @Transactional
    public void insert(List<ExecutionPlan> executionPlanList) {
        executionPlanMapper.insert(executionPlanList);
    }

    @Override
    @Transactional
    public void update(ExecutionPlan executionPlan) {
        executionPlanMapper.updateByPrimaryKey(executionPlan);
    }

    @Override
    public PageInfo<ExecutionPlan> getExceptionExecutionPlan() {
        return null;
    }
}
