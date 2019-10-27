package xyz.kingsword.course.pojo.param;

import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ExecutionPlanSearchParam {
    private Integer id;

    private String semesterId;

    private Integer grade;

    private Integer specialityId;

    private String courseName;

    @Builder.Default
    private int pageNum = 1;

    @Builder.Default
    private int pageSize = 10;
}
