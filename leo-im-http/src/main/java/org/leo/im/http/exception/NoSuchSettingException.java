package org.leo.im.http.exception;

/**
 * 无此配置异常类
 * 
 * @author Leo
 * @date 2018/5/11
 */
public final class NoSuchSettingException extends RuntimeException {

    private static final long serialVersionUID = 1911497055661761968L;
    
    public NoSuchSettingException() {
    }

    public NoSuchSettingException(String message) {
        super(message);
    }

    public NoSuchSettingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSettingException(Throwable cause) {
        super(cause);
    }

}
