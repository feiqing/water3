package cn.anne.exception;

/**
 * @author qingfei
 * @date 2022/05/16
 */
public class WaterException extends RuntimeException {

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
