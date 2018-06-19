package org.leo.im.http.interceptor;

import org.leo.web.rest.HttpResponse;
import org.leo.web.rest.interceptor.Interceptor;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 跨域拦截器
 * 
 * @author Leo
 * @date 2018/4/2
 */
public final class CorsInterceptor implements Interceptor {

    @Override
    public boolean preHandle(FullHttpRequest request, HttpResponse response) throws Exception {
        // 使用axios发送cookie，这里不能用*，需要使用Web前端地址，如：http://localhost:8080
        // response.getHeaders().put("Access-Control-Allow-Origin", "*");
        response.getHeaders().put("Access-Control-Allow-Origin", System.getProperty("http.origin"));
        response.getHeaders().put("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
        response.getHeaders().put("Access-Control-Max-Age", "3600");
        response.getHeaders().put("Access-Control-Allow-Headers", "Content-Type,X-Token");
        response.getHeaders().put("Access-Control-Allow-Credentials", "true");
        return true;
    }

    @Override
    public void postHandle(FullHttpRequest request, HttpResponse response) throws Exception {
    }

    @Override
    public void afterCompletion(FullHttpRequest request, HttpResponse response) {
    }

}
