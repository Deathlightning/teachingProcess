package xyz.kingsword.course.pojo.param;

import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CourseGroupSelectParam {
    private Integer sortId;

    private String semesterId;

    private String courseId;

    private String teaId;
}
