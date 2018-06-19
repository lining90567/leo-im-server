package org.leo.im.api.provider;

import org.leo.im.api.service.ChannelService;
import org.leo.im.api.service.MessageService;
import org.leo.im.api.service.UserChannelService;
import org.leo.im.api.service.UserService;
import org.leo.im.service.ChannelServiceImpl;
import org.leo.im.service.MessageServiceImpl;
import org.leo.im.service.UserChannelServiceImpl;
import org.leo.im.service.UserServiceImpl;

/**
 * 服务工厂类
 * 
 * @author Leo
 * @date 2018/3/30
 */
public final class ServiceFactory {

    /**
     * 创建用户服务的实例
     * 
     * @return
     */
    public static UserService createUserService() {
        return new UserServiceImpl();
    }
    
    /**
     * 创建频道服务的实例
     * @return
     */
    public static ChannelService createChannelService() {
        return new ChannelServiceImpl();
    }
    
    /**
     * 创建用户频道服务类的实例
     * @return
     */
    public static UserChannelService createUserChannelService() {
        return new UserChannelServiceImpl();
    }
    
    /**
     * 创建消息服务的实例
     * @return
     */
    public static MessageService createMessageService() {
        return new MessageServiceImpl();
    }

}
