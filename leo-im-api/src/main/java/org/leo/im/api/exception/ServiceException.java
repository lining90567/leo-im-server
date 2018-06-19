package org.leo.im.api.exception;

/**
 * 服务异常类
 * 
 * @author Leo
 * @date 2018/3/20
 */
public final class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 2447167264295739984L;

    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

}
