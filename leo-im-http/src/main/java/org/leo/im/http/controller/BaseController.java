package org.leo.im.http.controller;

import org.leo.im.http.util.JwtUtils;

import com.alibaba.fastjson.JSONObject;

import io.jsonwebtoken.Claims;

/**
 * 控制器基类
 * 
 * @author Leo
 * @date 2018/3/26
 */
public class BaseController {
    
    /**
     * 从jwt中获取subject信息
     * @param jwtUtils
     * @param jwt
     * @param key
     * @return
     */
    protected String getSubjectFromJwt(String jwt, String key) {
        Claims claims = JwtUtils.parseJWT(jwt, System.getProperty("jwt.secret"));
        String subject = claims.getSubject();
        if(key != null && !key.trim().equals("")) {
            JSONObject json = JSONObject.parseObject(subject);
            return json.getString(key);
        } else {
            return subject;
        }
    }
    
    /**
     * 验证jwt
     * @param jwt
     * @return
     */
    protected boolean verifyJwt(String jwt) {
        try {
            JwtUtils.parseJWT(jwt, System.getProperty("jwt.secret"));
            return true;
        } catch(Exception e) {
            return false;
        }
    }

}
