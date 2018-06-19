package org.leo.im.store.exception;

/**
 * dao异常类
 * 
 * @author Leo
 * @date 2018/3/19
 */
public final class DAOException extends RuntimeException {

    private static final long serialVersionUID = 4070326969626499752L;
    
    public DAOException() {
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

}
