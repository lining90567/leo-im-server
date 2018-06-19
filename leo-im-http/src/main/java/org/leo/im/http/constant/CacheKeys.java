package org.leo.im.http.constant;

/**
 * 缓存key常量类
 * 
 * @author Leo
 * @date 2018/4/1
 */
public final class CacheKeys {

    /**
     * JSession key前缀
     */
    public static final String JSESSIONID = "JSESSIONID";
    
    /**
     * 验证码缓存key前缀
     */
    public static final String VERIFICATION_CODE_PREFIX = "v-code-"; 
    
    /**
     * 登录失败次数缓存前缀
     */
    public static final String LOGIN_FAILURE_COUNT = "login-failure-count_";
    
}
