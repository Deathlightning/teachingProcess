package xyz.kingsword.course.pojo.param;

import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CalendarSelectParam {
    private int id;

    private String teacherId;

    private String semesterId;

    private String courseId;

    @Builder.Default
    private int pageNum = 1;
    @Builder.Default
    private int pageSize = 10;
}
