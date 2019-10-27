package xyz.kingsword.course.pojo;

import lombok.*;

@Builder
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BookOrder {
    private String studentId;

    private String semesterId;

    private int bookId;
}
