package org.leo.im.http.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.leo.im.api.service.UserService;
import org.leo.im.api.dto.UserDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.http.cache.CacheManagerFactory;
import org.leo.im.http.constant.CacheKeys;
import org.leo.im.http.util.JwtUtils;
import org.leo.im.service.support.ServiceProxy;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RestController;
import org.leo.web.annotation.UrlEncodedForm;
import org.leo.web.rest.HttpResponse;
import org.leo.web.rest.HttpStatus;
import org.leo.web.rest.ResponseEntity;

import com.alibaba.fastjson.JSONObject;

import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 认证控制器
 * 
 * @author Leo
 * @date 2018/4/4
 */
@RestController
@RequestMapping("/auth")
public final class AuthController extends BaseController {
    
    /**
     * Session超时时间，单位：秒。
     */
    private static final int SESSION_TIMEOUT = 5 * 60;
    
    /**
     * 验证码超时时间，默认为120秒。
     */
    private static final int VERIFICATION_CODE_TIMEOUT = 2 * 60;
    
    /**
     * 登录重试次数
     */
    private static final int LOGIN_RETRY_COUNT = 5;
    
    /**
     * 登录失败次数缓存超时时间，单位：秒
     */
    private static final int LOGIN_FAILURE_COUNT_TIMEOUT = 5 * 60;

    @PostMapping("/login")
    public ResponseEntity<?> login(FullHttpRequest request, HttpResponse response, @UrlEncodedForm Map<String, String> form) {
        String sessionId = getJSessionId(request);
        if(sessionId == null) {
            // Session不存在
            sessionId = UUID.randomUUID().toString().replaceAll("-", "");
            response.getCookies().put(CacheKeys.JSESSIONID, sessionId);
        }
        CacheManagerFactory.getCacheManager().put(sessionId, (byte)0, SESSION_TIMEOUT);
        
        // 检查登录失败次数
        Object loginFailureCount = CacheManagerFactory.getCacheManager().get(CacheKeys.LOGIN_FAILURE_COUNT + sessionId);
        if(loginFailureCount != null) {
            if((int)loginFailureCount >= LOGIN_RETRY_COUNT) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build("登录失败次数过多，请稍后重试");
            }
        }
        
        // 检查验证码
        /*String verificationCode = form.get("verificationCode");
        if(verificationCode == null || verificationCode.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("验证码为空");
        }
        if(sessionId == null || sessionId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("无效的请求");
        }
        Object verificationCodeInCache = CacheManagerFactory.getCacheManager().get(CacheKeys.VERIFICATION_CODE_PREFIX + sessionId);
        if(verificationCodeInCache == null || verificationCodeInCache.toString().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("无效的验证码");
        }
        if(!verificationCode.equals(verificationCodeInCache)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("无效的验证码");
        }*/
        
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        UserDTO dto = serviceProxy.verifyLogin(form.get("username"), form.get("password"));        
        if (dto == null) {
            int userLoginFailureCount = (loginFailureCount == null ? 0 : (int)loginFailureCount);
            CacheManagerFactory.getCacheManager().put(CacheKeys.LOGIN_FAILURE_COUNT + sessionId, ++userLoginFailureCount, LOGIN_FAILURE_COUNT_TIMEOUT);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }
        
        JSONObject json = new JSONObject();
        json.put("userId", dto.getId());
        String jwt = JwtUtils.createJWT(json.toJSONString(), System.getProperty("jwt.secret"), Long.getLong("jwt.ttl.millis"));
        Map<String, String> result = new HashMap<>();
        result.put("userId", dto.getId());
        result.put("username", dto.getName());
        result.put("nickname", dto.getNickname());
        result.put("firstLetterOfName", dto.getFirstLetterOfName());
        result.put("avatarUrl", dto.getAvatarUrl());
        result.put("token", jwt);
        
        // 删除缓存中的验证码
        // CacheManagerFactory.getCacheManager().remove(CacheKeys.VERIFICATION_CODE_PREFIX + sessionId);
        // 删除登录失败次数缓存
        CacheManagerFactory.getCacheManager().remove(CacheKeys.LOGIN_FAILURE_COUNT + sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/verificationCode")
    public ResponseEntity<String> getVerificationCode(FullHttpRequest request, HttpResponse response) {
        String verificationCode = UUID.randomUUID().toString().substring(0, 4);
        String sessionId = null;
        if((sessionId = getJSessionId(request)) == null) {
            // Session不存在
            sessionId = UUID.randomUUID().toString().replaceAll("-", "");
            response.getCookies().put(CacheKeys.JSESSIONID, sessionId);
        }
        CacheManagerFactory.getCacheManager().put(sessionId, (byte)0, SESSION_TIMEOUT);
        CacheManagerFactory.getCacheManager().put(CacheKeys.VERIFICATION_CODE_PREFIX + sessionId, verificationCode, VERIFICATION_CODE_TIMEOUT);
        return ResponseEntity.ok(verificationCode);
    }

    /**
     * 从cookie中得到Session Id
     * @param request
     * @return
     */
    private String getJSessionId(FullHttpRequest request) {
        try {
            String cookieStr = request.headers().get("Cookie");
            if(cookieStr == null || cookieStr.trim().isEmpty()) {
                return null;
            }
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            Iterator<Cookie> it = cookies.iterator();

            while (it.hasNext()) {
                Cookie cookie = it.next();
                if (cookie.name().equals(CacheKeys.JSESSIONID)) {
                    if (CacheManagerFactory.getCacheManager().get(cookie.value()) != null) {
                        return cookie.value();
                    }
                }
            }
        } catch (Exception e1) {
            return null;
        }
        return null;
    }

}
