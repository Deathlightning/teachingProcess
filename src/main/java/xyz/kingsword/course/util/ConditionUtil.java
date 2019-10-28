package xyz.kingsword.course.util;

import java.util.function.Supplier;

public class ConditionUtil {
    private final boolean flag;

    private ConditionUtil(boolean flag) {
        this.flag = flag;
    }

    public static ConditionUtil validateTrue(boolean flag) {
        return new ConditionUtil(flag);
    }


    public <X extends Throwable> void orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (!flag)
            throw exceptionSupplier.get();
    }

}
