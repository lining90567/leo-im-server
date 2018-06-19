package org.leo.im.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;

/**
 * 事务注解，该注解需要在服务接口上使用，在实现类上使用无效。
 * 
 * @author Leo
 * @date 2018/3/19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Inherited
public @interface Transactional {
}
