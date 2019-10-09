package xyz.kingsword.course.exception;

import xyz.kingsword.course.VO.ResultVO;
import xyz.kingsword.course.enmu.ErrorEnum;

public class CourseException extends RuntimeException {

    private ResultVO resultVO;

    /**
     * 调用时可以在任何代码处直接throws这个Exception,
     * 都会统一被拦截,并封装好json返回给前台
     *
     * @param errorEnum 以错误的ErrorEnum做参数
     */
    public CourseException(ErrorEnum errorEnum) {
        this.resultVO = new ResultVO();
        this.resultVO.setCode(errorEnum.getCode());
        this.resultVO.setMsg(errorEnum.getMsg());
    }

    public CourseException() {
    }
}
