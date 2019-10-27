package xyz.kingsword.course.pojo.param;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@ApiModel(description = "查询参数，可任意组合")
public class CourseSelectParam {
    private String courseId;

    private String courseName;

    private String researchRoom;

    private String teacherInCharge;

    @Builder.Default
    private int pageNum = 1;
    @Builder.Default
    private int pageSize = 10;
}
