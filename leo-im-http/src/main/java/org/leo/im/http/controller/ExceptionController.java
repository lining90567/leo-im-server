package org.leo.im.http.controller;

import org.leo.web.exception.ResourceNotFoundException;
import org.leo.web.rest.HttpContextHolder;
import org.leo.web.rest.HttpResponse;
import org.leo.web.rest.HttpStatus;
import org.leo.web.rest.controller.ExceptionHandler;

/**
 * 异常处理器
 * 
 * @author Leo
 * @date 2018/3/30
 */
public class ExceptionController implements ExceptionHandler {

    /**
     * 处理异常
     * 
     * @param e
     */
    @Override
    public void doHandle(Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (e instanceof ResourceNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        }
        String errorMessage = e.getCause() == null ? "" : e.getCause().getMessage();
        if (errorMessage == null) {
            errorMessage = e.getMessage();
        }
        HttpResponse response = HttpContextHolder.getResponse();
        response.write(status, errorMessage);
        response.closeChannel();
    }

}
