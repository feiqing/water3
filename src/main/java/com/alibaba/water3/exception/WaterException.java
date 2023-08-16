package com.alibaba.water3.exception;

/**
 * @author qingfei
 * @date 2022/05/16
 */
public class WaterException extends RuntimeException {

    private static final long serialVersionUID = -2060948238287068976L;

    public WaterException() {
    }

    public WaterException(String message) {
        super(message);
    }

    public WaterException(String message, Throwable cause) {
        super(message, cause);
    }

    public WaterException(Throwable cause) {
        super(cause);
    }

    public WaterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
