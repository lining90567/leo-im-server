package org.leo.im.socket.subscription.handler;

/**
 * 昵称改变消息处理器
 * 
 * @author Leo
 * @date 2018/5/15
 */
public class NicknameChangedHandler extends AbstractMessageHandler implements MessageHandler {
    
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
