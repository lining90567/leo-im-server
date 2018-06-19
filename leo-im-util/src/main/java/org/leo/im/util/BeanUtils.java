package org.leo.im.util;

import net.sf.cglib.beans.BeanCopier;

/**
 * Java Bean 工具类
 * 
 * @author Leo
 * @date 2018/3/20
 */
public final class BeanUtils {

    /**
     * 拷贝属性
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        BeanCopier copier = BeanCopier.create(source.getClass(), target.getClass(), false);
        copier.copy(source, target, null);
    }
    
}
