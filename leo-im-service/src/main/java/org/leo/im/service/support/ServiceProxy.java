package org.leo.im.service.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

import org.leo.im.api.annotation.Cacheable;
import org.leo.im.api.annotation.Transactional;
import org.leo.im.store.connection.impl.PoolConnectionFactory;

/**
 * 服务代理类
 * 
 * @author Leo
 * @date 2018/3/23
 */
public final class ServiceProxy implements InvocationHandler {

    /**
     * 代理目标
     */
    private Object target;

    private ServiceProxy(Object target) {
        this.target = target;
    }

    /**
     * 通过Class来生成动态代理对象Proxy
     * 
     * @param target
     * @param connectionString
     *            数据库连接字符串
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T newProxyInstance(Object target) {
        return (T) java.lang.reflect.Proxy.newProxyInstance(target.getClass().getClassLoader(),
                target.getClass().getInterfaces(), new ServiceProxy(target));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 得到数据库连接
        Connection conn = PoolConnectionFactory.getInstance().getConnection();

        // 判断方法是否启用了缓存注解
        CacheableHolder.setCacheable(method.getAnnotation(Cacheable.class) != null);

        // 是否启用了事务注解
        if (method.getAnnotation(Transactional.class) == null) {
            // 执行业务逻辑
            try {
                return method.invoke(this.target, args);
            } finally {
                PoolConnectionFactory.getInstance().closeConnection();
                CacheableHolder.remove();
            }
        }

        conn.setAutoCommit(false);
        try {
            Object obj = method.invoke(this.target, args);
            conn.commit();
            return obj;
        } catch (Throwable t) {
            conn.rollback();
            throw t;
        } finally {
            PoolConnectionFactory.getInstance().closeConnection();
            CacheableHolder.remove();
        }
    }

}
