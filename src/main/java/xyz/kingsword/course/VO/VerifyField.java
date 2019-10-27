package xyz.kingsword.course.VO;

import lombok.Getter;

/**
 * 每个参数是否相同
 */
@Getter
public class VerifyField {
    private final String name;
    private final boolean same;

    public VerifyField(String name, boolean same) {
        this.name = name;
        this.same = same;
    }
}
