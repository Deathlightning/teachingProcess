package xyz.kingsword.course.pojo.param.sortCourse;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@NoArgsConstructor
public class SearchParam {
    private String teaId;

    private String couId;
    private String courseName;
    private Integer classroomId;
    /**
     * 学期id
     */
    private String semesterId;

    private int pageNum;
    private int pageSize;
}
