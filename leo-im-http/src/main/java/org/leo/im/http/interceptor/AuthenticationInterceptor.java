package org.leo.im.http.interceptor;

import org.leo.im.http.util.JwtUtils;
import org.leo.web.rest.HttpResponse;
import org.leo.web.rest.HttpStatus;
import org.leo.web.rest.interceptor.Interceptor;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * 身份认证拦截器
 * 
 * @author Leo
 * @date 2018/4/3
 */
public class AuthenticationInterceptor implements Interceptor {

    @Override
    public boolean preHandle(FullHttpRequest request, HttpResponse response) throws Exception {
        if (request.method().name().equalsIgnoreCase("OPTIONS")) {
            return true;
        }
        try {
            JwtUtils.parseJWT(request.headers().get("X-Token"), System.getProperty("jwt.secret"));
            return true;
        } catch (Exception e) {
            response.write(HttpStatus.UNAUTHORIZED, "Unauthorized");
            return false;
        }
    }

    @Override
    public void postHandle(FullHttpRequest request, HttpResponse response) throws Exception {
    }

    @Override
    public void afterCompletion(FullHttpRequest request, HttpResponse response) {
    }

}
