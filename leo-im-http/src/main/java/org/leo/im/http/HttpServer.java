package org.leo.im.http;

import org.leo.im.http.controller.ExceptionController;
import org.leo.im.http.interceptor.AuthenticationInterceptor;
import org.leo.im.http.interceptor.CorsInterceptor;
import org.leo.web.core.WebServer;

/**
 * Leo IM REST API Server
 * 
 * @author Leo
 * @date 2018/4/2
 */
public final class HttpServer {
    
    /**
     * 启动api server
     * @throws InterruptedException 
     */
    public void start() throws InterruptedException {
        // 忽略指定url
        WebServer.getIgnoreUrls().add("/favicon.ico");
        
        // 全局异常处理
        WebServer.setExceptionHandler(new ExceptionController());
        
        // 设置监听端口号
        WebServer server = new WebServer(Integer.getInteger("http.port"));
        
        // 设置boss与worker线程数
        server.setBossThreads(Integer.getInteger("http.boss.threads"));
        server.setWorkerThreads(Integer.getInteger("http.worker.threads"));
        
        // 设置Http最大内容长度（默认 为10M）
        server.setMaxContentLength(1024 * 1024 * Integer.getInteger("http.max.content.length"));
        
        // 设置Controller所在包
        server.setControllerBasePackage("org.leo.im.http.controller");
        
        // 添加拦截器，按照添加的顺序执行。
        // 跨域拦截器
        server.addInterceptor(new CorsInterceptor());
        // 身份认证拦截器，设置例外url
        server.addInterceptor(new AuthenticationInterceptor(), "/auth/login", "/auth/verificationCode", 
                "/users", "/messages/files");
        
        server.start();
    }

}
