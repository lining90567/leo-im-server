package org.leo.im.socket.exception;

/**
 * 消息处理异常类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public final class MessageHandleException extends RuntimeException {

    private static final long serialVersionUID = -2512674268771543792L;
    
    public MessageHandleException() {
    }

    public MessageHandleException(String message) {
        super(message);
    }

    public MessageHandleException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageHandleException(Throwable cause) {
        super(cause);
    }

}
