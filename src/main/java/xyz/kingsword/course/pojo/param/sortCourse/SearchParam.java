package xyz.kingsword.course.pojo.param.sortCourse;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@NoArgsConstructor
@ApiModel(value = "SearchParam",description = "查询参数，可任意组合")
public class SearchParam {
    private String teaId;

    private String couId;
    private String courseName;
    private Integer classroomId;
    /**
     * 学期id
     */
    private String semesterId;

    private int pageNum = 1;
    private int pageSize = 10;

    /**
     * 排课标志，已分配为1，未分配-1，全部为0
     */
    @ApiModelProperty(value = "排课标志，已分配为1，未分配-1，全部为0", allowableValues = "range[-1,0,1]",required = true,example = "0")
    private int sortCourseFlag = 0;
}
