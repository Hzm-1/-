package model.utils;

import java.math.BigInteger;

/**
 * 线程本地存储
 */
public class CurrentHolder {
    private static final ThreadLocal<BigInteger> CURRENT_LOCAL = new ThreadLocal<>();

    public static void setCurrentId(BigInteger employeeId) {
        CURRENT_LOCAL.set(employeeId);
    }

    public static BigInteger getCurrentId() {
        return CURRENT_LOCAL.get();
    }

    public static void remove() {
        CURRENT_LOCAL.remove();
    }
}
