package org.leo.im.socket.subscription.handler;

/**
 * 头像改变消息处理器
 * @author Administrator
 *
 */
public class AvatarChangedHandler extends AbstractMessageHandler implements MessageHandler {
    
    /**
     * 处理消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        super.setGroups("all");
        super.handleMessage(message);
    }

}
